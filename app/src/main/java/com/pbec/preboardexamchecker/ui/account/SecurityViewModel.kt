package com.pbec.preboardexamchecker.ui.account

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.firestore.FirebaseFirestoreException
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SecurityViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val preferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    private val auth = FirebaseAuth.getInstance()
    private var owner = auth.currentUser
    private var operation = 0L
    private fun pendingKey(uid: String) = "pending_email_$uid"
    private val _emailState = MutableStateFlow(EmailChangeState(
        currentEmail = auth.currentUser?.email.orEmpty(),
        pendingEmail = owner?.uid?.let { preferences.getString(pendingKey(it), null) }
    ))
    val emailState: StateFlow<EmailChangeState> = _emailState.asStateFlow()

    private val authListener = FirebaseAuth.AuthStateListener { firebase ->
        if (owner !== firebase.currentUser) {
            owner = firebase.currentUser
            operation++
            _isUpdatingPassword.value = false
            _emailState.value = EmailChangeState(currentEmail = owner?.email.orEmpty(),
                pendingEmail = owner?.uid?.let { preferences.getString(pendingKey(it), null) })
        }
    }

    override fun onCleared() {
        operation++
        auth.removeAuthStateListener(authListener)
        super.onCleared()
    }

    fun clearEmailFieldError(field: EmailField) {
        _emailState.value = when (field) {
            EmailField.EMAIL -> _emailState.value.copy(emailError = null, invalidField = null)
            EmailField.PASSWORD -> _emailState.value.copy(passwordError = null, invalidField = null)
        }
    }

    fun requestEmailChange(newEmail: String, password: String, onSent: () -> Unit) {
        if (_emailState.value.busy || _isUpdatingPassword.value) return
        if (_emailState.value.needsProfileSync) {
            _emailState.value = _emailState.value.copy(message = "Check verification status to finish syncing your account before requesting another email change.")
            return
        }
        val user = auth.currentUser
        val email = newEmail.trim()
        val emailError = when {
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Enter a valid email address."
            email.equals(user?.email, ignoreCase = true) -> "Enter an email address different from your current email."
            else -> null
        }
        val passwordError = if (password.isBlank()) "Enter your current password." else null
        if (emailError != null || passwordError != null) {
            _emailState.value = _emailState.value.copy(emailError = emailError, passwordError = passwordError,
                invalidField = if (emailError != null) EmailField.EMAIL else EmailField.PASSWORD,
                validationAttempt = _emailState.value.validationAttempt + 1, message = null)
            return
        }
        val error = when {
            user == null || user.isAnonymous || user.email.isNullOrBlank() -> "Please sign in again to change your email."
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Enter a valid email address."
            email.equals(user.email, ignoreCase = true) -> "Enter an email address different from your current email."
            password.isBlank() -> "Enter your current password."
            else -> null
        }
        if (error != null) {
            _emailState.value = _emailState.value.copy(message = error)
            return
        }
        val signedInUser = user ?: return
        val request = ++operation
        fun active() = request == operation && auth.currentUser === signedInUser
        _emailState.value = _emailState.value.copy(busy = true, action = EmailAction.SEND_LINK, message = null, emailError = null, passwordError = null, invalidField = null)
        signedInUser.reauthenticate(EmailAuthProvider.getCredential(signedInUser.email!!, password))
            .addOnSuccessListener {
                if (!active()) return@addOnSuccessListener
                signedInUser.verifyBeforeUpdateEmail(email)
                    .addOnSuccessListener sent@ {
                        if (!active()) return@sent
                        preferences.edit().putString(pendingKey(signedInUser.uid), email).apply()
                        _emailState.value = _emailState.value.copy(busy = false, action = null, pendingEmail = email,
                            message = "Verification email sent. Your email has not changed yet.")
                        onSent()
                    }
                    .addOnFailureListener { if (active()) emailFailure("email_verification", it) }
            }
            .addOnFailureListener { if (active()) emailFailure("email_reauthentication", it) }
    }

    fun refreshPendingEmailStatus() {
        if (_emailState.value.pendingEmail != null || _emailState.value.needsProfileSync) checkEmailStatus()
    }

    fun checkEmailStatus() {
        if (_emailState.value.busy || _isUpdatingPassword.value) return
        val user = auth.currentUser
        if (user == null || user.isAnonymous) {
            _emailState.value = _emailState.value.copy(message = "Sign in with your verified email to finish updating your account.")
            return
        }
        val request = ++operation
        val pending = _emailState.value.pendingEmail
        _emailState.value = _emailState.value.copy(busy = true, action = EmailAction.CHECK_STATUS, message = null)
        val service = object : EmailProfileSync.Service {
            override fun isCurrentSession() = request == operation && auth.currentUser === user
            override fun reload(callback: (Result<String>) -> Unit) {
                user.reload().addOnSuccessListener { callback(Result.success(user.email.orEmpty())) }
                    .addOnFailureListener { callback(Result.failure(it)) }
            }
            override fun refreshToken(callback: (Result<Unit>) -> Unit) {
                user.getIdToken(true).addOnSuccessListener { callback(Result.success(Unit)) }
                    .addOnFailureListener { callback(Result.failure(it)) }
            }
            override fun updateProfile(email: String, callback: (Result<Unit>) -> Unit) {
                firestore.collection("users").document(user.uid).update("email", email)
                    .addOnSuccessListener { callback(Result.success(Unit)) }
                    .addOnFailureListener { callback(Result.failure(it)) }
            }
            override fun commit(email: String, clearPending: Boolean) {
                preferences.edit().putString("teacher_email", email).apply {
                    if (clearPending) remove(pendingKey(user.uid))
                }.apply()
            }
        }
        EmailProfileSync(service).check(pending, onVerified = { email ->
            _emailState.value = _emailState.value.copy(currentEmail = email, needsProfileSync = true)
        }) { outcome ->
            when (outcome) {
                is EmailProfileSync.Outcome.Unverified -> _emailState.value = _emailState.value.copy(
                    busy = false, action = null, currentEmail = outcome.email, needsProfileSync = false,
                    message = "Your new email is not verified yet. Open the link in your verification email, then check again.")
                is EmailProfileSync.Outcome.Synced -> _emailState.value = _emailState.value.copy(
                    busy = false, action = null, currentEmail = outcome.email, needsProfileSync = false,
                    pendingEmail = if (outcome.verified) null else pending,
                    message = if (outcome.verified) "Email updated successfully." else "Your account email is up to date.")
                is EmailProfileSync.Outcome.Failed -> {
                    emailFailure(outcome.stage, outcome.error as? Exception ?: Exception(outcome.error))
                    _emailState.value = _emailState.value.copy(needsProfileSync = true,
                        message = if (outcome.verifiedEmail != null)
                            "Your email is verified, but your profile could not sync. Check your connection and try again, or sign in with your new email."
                        else _emailState.value.message)
                }
            }
        }
    }

    private fun emailFailure(stage: String, error: Exception) {
        val code = (error as? FirebaseAuthException)?.errorCode
        Log.w("TeacherSecurity", "stage=$stage code=${code ?: error.javaClass.simpleName}")
        val message = when {
            error is FirebaseNetworkException -> "Check your connection and try again."
            error is FirebaseTooManyRequestsException -> "Too many attempts. Please try again later."
            code in listOf("ERROR_WRONG_PASSWORD", "ERROR_INVALID_CREDENTIAL", "ERROR_INVALID_LOGIN_CREDENTIALS") -> "Current password is incorrect."
            code == "ERROR_INVALID_EMAIL" -> "Enter a valid email address."
            code == "ERROR_EMAIL_ALREADY_IN_USE" -> "This email is already used by another account."
            code in listOf("ERROR_REQUIRES_RECENT_LOGIN", "ERROR_USER_TOKEN_EXPIRED", "ERROR_INVALID_USER_TOKEN") -> "Please sign in again. If you verified your new email, use it to sign in and finish updating your account."
            else -> "Unable to update your email. Try again or contact an administrator."
        }
        val field = when (code) {
            "ERROR_WRONG_PASSWORD", "ERROR_INVALID_CREDENTIAL", "ERROR_INVALID_LOGIN_CREDENTIALS" -> EmailField.PASSWORD
            "ERROR_INVALID_EMAIL", "ERROR_EMAIL_ALREADY_IN_USE" -> EmailField.EMAIL
            else -> null
        }
        _emailState.value = _emailState.value.copy(busy = false, action = null, message = if (field == null) message else null,
            emailError = if (field == EmailField.EMAIL) message else null,
            passwordError = if (field == EmailField.PASSWORD) message else null,
            invalidField = field, validationAttempt = _emailState.value.validationAttempt + 1)
    }

    private val _isUpdatingPassword = MutableStateFlow(false)
    val isUpdatingPassword: StateFlow<Boolean> = _isUpdatingPassword.asStateFlow()

    init { auth.addAuthStateListener(authListener) }

    private val _uiEvent = MutableSharedFlow<UiEvent>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val uiEvent: SharedFlow<UiEvent> = _uiEvent.asSharedFlow()

    fun changePassword(currentPassword: String, newPassword: String, confirmPassword: String, onSuccess: () -> Unit) {
        if (_isUpdatingPassword.value || _emailState.value.busy) return
        val sessionUser = auth.currentUser
        if (sessionUser == null || sessionUser.isAnonymous) {
            _uiEvent.tryEmit(UiEvent.ShowSnackbar("Please sign in again before changing your password."))
            return
        }
        val teacherId = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            .getString("teacher_id", null)
            ?.trim()
            ?.uppercase()

        if (teacherId.isNullOrBlank()) {
            _uiEvent.tryEmit(UiEvent.ShowSnackbar("Teacher account not found. Please login again."))
            return
        }
        if (currentPassword.isBlank() || newPassword.isBlank() || confirmPassword.isBlank()) {
            _uiEvent.tryEmit(UiEvent.ShowSnackbar("All password fields are required."))
            return
        }
        if (newPassword != confirmPassword) {
            _uiEvent.tryEmit(UiEvent.ShowSnackbar("New password and confirmation do not match."))
            return
        }
        if (currentPassword == newPassword) {
            _uiEvent.tryEmit(UiEvent.ShowSnackbar("New password must be different from current password."))
            return
        }

        _isUpdatingPassword.value = true
        val request = ++operation
        fun active() = request == operation && auth.currentUser === sessionUser
        lookupTeacherDocument(teacherId) { teacherDocument, errorMessage ->
            if (!active()) return@lookupTeacherDocument
            if (teacherDocument == null) {
                _isUpdatingPassword.value = false
                _uiEvent.tryEmit(UiEvent.ShowSnackbar(errorMessage ?: "Teacher account not found."))
                return@lookupTeacherDocument
            }

            val authUser = FirebaseAuth.getInstance().currentUser
            val email = authUser?.email ?: teacherDocument.getString("email")
            if (authUser == null || authUser.isAnonymous || email.isNullOrBlank() || authUser.uid != teacherDocument.id) {
                _isUpdatingPassword.value = false
                _uiEvent.tryEmit(UiEvent.ShowSnackbar("Authenticated teacher account not found. Please login again."))
                return@lookupTeacherDocument
            }

            authUser.reauthenticate(EmailAuthProvider.getCredential(email, currentPassword))
                .addOnSuccessListener {
                    if (!active()) return@addOnSuccessListener
                    authUser.updatePassword(newPassword)
                        .addOnSuccessListener updated@ {
                            if (!active()) return@updated
                            _isUpdatingPassword.value = false
                            _uiEvent.tryEmit(UiEvent.ShowSnackbar("Password updated successfully."))
                            onSuccess()
                        }
                        .addOnFailureListener { error ->
                            if (!active()) return@addOnFailureListener
                            _isUpdatingPassword.value = false
                            _uiEvent.tryEmit(UiEvent.ShowSnackbar(failureMessage("password_update", error)))
                        }
                }
                .addOnFailureListener { error ->
                    if (!active()) return@addOnFailureListener
                    _isUpdatingPassword.value = false
                    _uiEvent.tryEmit(UiEvent.ShowSnackbar(failureMessage("reauthentication", error)))
                }
        }
    }

    private fun lookupTeacherDocument(teacherId: String, onComplete: (DocumentSnapshot?, String?) -> Unit) {
        firestore.collection("users")
            .whereEqualTo("teacherId", teacherId)
            .limit(1)
            .get()
            .addOnSuccessListener { teacherSnapshot ->
                if (!teacherSnapshot.isEmpty) {
                    onComplete(teacherSnapshot.documents.firstOrNull(), null)
                    return@addOnSuccessListener
                }

                firestore.collection("users")
                    .whereEqualTo("instructorId", teacherId)
                    .limit(1)
                    .get()
                    .addOnSuccessListener { instructorSnapshot ->
                        onComplete(
                            instructorSnapshot.documents.firstOrNull(),
                            if (instructorSnapshot.isEmpty) "Teacher account not found." else null
                        )
                    }
                    .addOnFailureListener { error ->
                        onComplete(null, failureMessage("profile_lookup", error))
                    }
            }
            .addOnFailureListener { error ->
                onComplete(null, failureMessage("profile_lookup", error))
            }
    }

    private fun failureMessage(stage: String, error: Exception): String {
        val code = when (error) {
            is FirebaseAuthException -> error.errorCode
            is FirebaseFirestoreException -> error.code.name
            is FirebaseNetworkException -> "NETWORK_ERROR"
            is FirebaseTooManyRequestsException -> "ERROR_TOO_MANY_REQUESTS"
            else -> "UNKNOWN_ERROR"
        }
        Log.w("TeacherSecurity", "stage=$stage code=$code")
        val detail = when (code) {
            "ERROR_INVALID_CREDENTIAL", "ERROR_INVALID_LOGIN_CREDENTIALS", "ERROR_WRONG_PASSWORD" -> "Current password is incorrect."
            "ERROR_WEAK_PASSWORD" -> "New password does not meet the account password requirements."
            "ERROR_REQUIRES_RECENT_LOGIN", "ERROR_USER_TOKEN_EXPIRED", "ERROR_INVALID_USER_TOKEN" -> "Please sign in again before changing your password."
            "NETWORK_ERROR", "UNAVAILABLE" -> "Check your connection and try again."
            "ERROR_TOO_MANY_REQUESTS" -> "Too many attempts. Please try again later."
            else -> "Unable to complete the password change. Contact an administrator if it continues."
        }
        return "$detail (code: $code)"
    }

    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
    }
}

data class EmailChangeState(
    val currentEmail: String = "",
    val pendingEmail: String? = null,
    val busy: Boolean = false,
    val action: EmailAction? = null,
    val message: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val invalidField: EmailField? = null,
    val validationAttempt: Int = 0,
    val needsProfileSync: Boolean = false
)

enum class EmailField { EMAIL, PASSWORD }

enum class EmailAction { SEND_LINK, CHECK_STATUS }
