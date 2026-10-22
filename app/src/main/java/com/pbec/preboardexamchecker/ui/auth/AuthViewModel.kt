package com.pbec.preboardexamchecker.ui.auth

import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestoreException
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val teacherIdRegex = Regex("^T\\d{4}-\\d{3}$")

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun clearError() {
        _errorMessage.value = null
    }

    fun login(instructorId: String, password: String, onSuccess: () -> Unit) {
        if (_isLoading.value) return
        if (instructorId.contains("@")) {
            loginWithEmail(instructorId.trim(), password, onSuccess)
            return
        }
        val normalizedInstructorId = instructorId.trim().uppercase()
        if (normalizedInstructorId.isBlank() || password.isBlank()) {
            _errorMessage.value = "Teacher ID or email and password are required."
            return
        }
        if (!teacherIdRegex.matches(normalizedInstructorId)) {
            _errorMessage.value = "Enter a Teacher ID like T2026-001 or your email address."
            return
        }

        _isLoading.value = true
        _errorMessage.value = null

        ensureFirebaseSession {
            firestore.collection("users")
                .whereEqualTo("teacherId", normalizedInstructorId)
                .limit(1)
                .get()
                .addOnSuccessListener { teacherSnapshot ->
                    if (!teacherSnapshot.isEmpty) {
                        startTeacherSession(
                            teacherDocument = teacherSnapshot.documents.firstOrNull(),
                            password = password,
                            onSuccess = onSuccess
                        )
                        return@addOnSuccessListener
                    }

                    // Backward compatibility for older records that used instructorId key.
                    firestore.collection("users")
                        .whereEqualTo("instructorId", normalizedInstructorId)
                        .limit(1)
                        .get()
                        .addOnSuccessListener instructorLookup@ { instructorSnapshot ->
                            if (instructorSnapshot.isEmpty) {
                                _isLoading.value = false
                                _errorMessage.value = "Teacher ID not found."
                                return@instructorLookup
                            }

                            startTeacherSession(
                                teacherDocument = instructorSnapshot.documents.firstOrNull(),
                                password = password,
                                onSuccess = onSuccess
                            )
                        }
                        .addOnFailureListener { error ->
                            _isLoading.value = false
                            _errorMessage.value = failureMessage("profile_lookup", error)
                        }
                }
                .addOnFailureListener { error ->
                    _isLoading.value = false
                    _errorMessage.value = failureMessage("profile_lookup", error)
                }
        }
    }

    private fun loginWithEmail(email: String, password: String, onSuccess: () -> Unit) {
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() || password.isBlank()) {
            _errorMessage.value = "Enter a valid email address and your password."
            return
        }
        _isLoading.value = true
        _errorMessage.value = null
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener { result ->
            val uid = result.user?.uid
            if (uid == null) {
                _isLoading.value = false
                _errorMessage.value = "Sign-in failed. Please try again."
                return@addOnSuccessListener
            }
            val signedInUser = result.user ?: return@addOnSuccessListener
            if (auth.currentUser !== signedInUser) return@addOnSuccessListener
            firestore.collection("users").document(uid).get()
                .addOnSuccessListener { document -> finishTeacherSession(document, signedInUser, onSuccess) }
                .addOnFailureListener {
                    if (auth.currentUser !== signedInUser) return@addOnFailureListener
                    auth.signOut()
                    _isLoading.value = false
                    _errorMessage.value = failureMessage("profile_lookup", it)
                }
        }.addOnFailureListener {
            _isLoading.value = false
            _errorMessage.value = failureMessage("password_sign_in", it)
        }
    }

    private fun finishTeacherSession(document: DocumentSnapshot, user: FirebaseUser, onSuccess: () -> Unit) {
        if (auth.currentUser !== user) return
        val teacherId = document.getString("teacherId") ?: document.getString("instructorId")
        val inactive = document.getBoolean("isActive") == false || document.getString("status").equals("inactive", true)
        if (!document.exists() || user.isAnonymous || user.uid != document.id || teacherId.isNullOrBlank() || inactive) {
            auth.signOut()
            _isLoading.value = false
            _errorMessage.value = if (inactive) "Account is inactive and pending admin activation." else "Teacher account identity does not match its profile. Contact an administrator."
            return
        }
        val email = user.email.orEmpty()
        if (email.isBlank()) {
            auth.signOut()
            _isLoading.value = false
            _errorMessage.value = "Teacher account has no sign-in email. Contact an administrator."
            return
        }
        val finish = finish@ {
            if (auth.currentUser !== user) return@finish
            persistTeacherSession(document, email)
            val preferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            val key = "pending_email_${user.uid}"
            if (preferences.getString(key, null).equals(email, true)) preferences.edit().remove(key).apply()
            _isLoading.value = false
            _errorMessage.value = null
            onSuccess()
        }
        if (document.getString("email") != email) {
            firestore.collection("users").document(user.uid).update("email", email)
                .addOnSuccessListener { finish() }
                .addOnFailureListener {
                    if (auth.currentUser !== user) return@addOnFailureListener
                    auth.signOut()
                    _isLoading.value = false
                    _errorMessage.value = "Your email is verified, but your profile could not sync. Sign in with your new email and try again. " + failureMessage("email_profile_sync", it)
                }
        } else finish()
    }

    fun register(
        fullName: String,
        school: String,
        position: String,
        email: String,
        onSuccess: (String, String) -> Unit
    ) {
        if (_isLoading.value) return
        if (fullName.isBlank() || school.isBlank() || position.isBlank() || email.isBlank()) {
            _errorMessage.value = "Full name, school, position, and email are required."
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
            _errorMessage.value = "Please enter a valid email address."
            return
        }

        _isLoading.value = true
        _errorMessage.value = null

        ensureFirebaseSession(requireAnonymous = true) {
            firestore.collection("users")
                .whereEqualTo("email", email.trim())
                .limit(1)
                .get()
                .addOnSuccessListener { snapshot ->
                    if (!snapshot.isEmpty) {
                        _isLoading.value = false
                        _errorMessage.value = "Email is already registered."
                        return@addOnSuccessListener
                    }

                    generateTeacherId { generatedTeacherId ->
                        val defaultPassword = deriveDefaultPasswordFromTeacherId(generatedTeacherId)
                        val now = Timestamp.now()
                        val activationDeadline = Timestamp(java.util.Date(now.toDate().time + 7L * 24 * 60 * 60 * 1000))
                        val anonymousUser = auth.currentUser
                        if (anonymousUser == null || !anonymousUser.isAnonymous) {
                            _isLoading.value = false
                            _errorMessage.value = "Registration requires a guest session. Sign out and try again."
                            return@generateTeacherId
                        }
                        val credential = EmailAuthProvider.getCredential(email.trim(), defaultPassword)
                        anonymousUser.linkWithCredential(credential)
                            .addOnSuccessListener linkSuccess@ { result ->
                                val uid = result.user?.uid
                                if (uid.isNullOrBlank()) {
                                    _isLoading.value = false
                                    _errorMessage.value = "Account creation failed: missing authenticated user."
                                    return@linkSuccess
                                }
                                firestore.collection("users")
                                    .document(uid)
                                    .set(
                                        mapOf(
                                            "teacherId" to generatedTeacherId,
                                            "name" to fullName.trim(),
                                            "school" to school.trim(),
                                            "position" to position.trim(),
                                            "department" to position.trim(),
                                            "email" to email.trim(),
                                            "role" to "teacher",
                                            "isActive" to false,
                                            "status" to "inactive",
                                            "activationDeadline" to activationDeadline,
                                            "createdAt" to now,
                                            "updatedAt" to now
                                        )
                                    )
                                    .addOnSuccessListener {
                                        _isLoading.value = false
                                        _errorMessage.value = null
                                        onSuccess(generatedTeacherId, defaultPassword)
                                    }
                                    .addOnFailureListener { error ->
                                        _isLoading.value = false
                                        _errorMessage.value = "Sign-in account was created, but the teacher profile could not be saved. Contact an administrator before registering again. " + failureMessage("profile_creation", error)
                                    }
                            }
                            .addOnFailureListener { error ->
                                _isLoading.value = false
                                _errorMessage.value = failureMessage("registration_link", error)
                            }
                    }
                }
                .addOnFailureListener { error ->
                    _isLoading.value = false
                    _errorMessage.value = failureMessage("profile_lookup", error)
                }
        }
    }

    private fun startTeacherSession(
        teacherDocument: DocumentSnapshot?,
        password: String,
        onSuccess: () -> Unit
    ) {
        if (teacherDocument == null) {
            _isLoading.value = false
            _errorMessage.value = "Teacher profile not found. Contact an administrator."
            return
        }
        val isActive = teacherDocument.getBoolean("isActive") ?: true
        if (!isActive || teacherDocument.getString("status")?.equals("inactive", ignoreCase = true) == true) {
            _isLoading.value = false
            val activationDeadline = teacherDocument.getTimestamp("activationDeadline")
            val deadlineMessage = activationDeadline?.toDate()?.let { " Activation deadline: $it." } ?: ""
            _errorMessage.value = "Account is inactive and pending admin activation.$deadlineMessage"
            return
        }
        val email = teacherDocument.getString("email").orEmpty().trim()
        if (email.isBlank()) {
            _isLoading.value = false
            _errorMessage.value = "Teacher account has no Firebase Auth email. Contact an administrator."
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val signedInUser = result.user ?: return@addOnSuccessListener
                if (auth.currentUser !== signedInUser) return@addOnSuccessListener
                if (result.user?.uid != teacherDocument.id) {
                    auth.signOut()
                    _isLoading.value = false
                    _errorMessage.value = "Teacher account identity does not match its profile. Contact an administrator."
                    return@addOnSuccessListener
                }
                finishTeacherSession(teacherDocument, signedInUser, onSuccess)
            }
            .addOnFailureListener { error ->
                _isLoading.value = false
                _errorMessage.value = failureMessage("password_sign_in", error)
            }
    }

    private fun ensureFirebaseSession(requireAnonymous: Boolean = false, onReady: () -> Unit) {
        val existingUser = auth.currentUser
        if (existingUser != null) {
            if (requireAnonymous && !existingUser.isAnonymous) {
                auth.signOut()
            } else {
                onReady()
                return
            }
        }

        auth.signInAnonymously()
            .addOnSuccessListener {
                onReady()
            }
            .addOnFailureListener { error ->
                _isLoading.value = false
                _errorMessage.value = failureMessage("anonymous_session", error)
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
        Log.w("TeacherAuth", "stage=$stage code=$code")
        val detail = when (code) {
            "ERROR_INVALID_CREDENTIAL", "ERROR_INVALID_LOGIN_CREDENTIALS", "ERROR_WRONG_PASSWORD", "ERROR_USER_NOT_FOUND" ->
                "Invalid email or password. Use your current Firebase account password."
            "ERROR_OPERATION_NOT_ALLOWED" -> if (stage == "anonymous_session")
                "Guest sign-in is unavailable. Ask an administrator to check Anonymous authentication."
            else "Email/password sign-in is unavailable. Ask an administrator to check Email/Password authentication."
            "ERROR_EMAIL_ALREADY_IN_USE", "ERROR_CREDENTIAL_ALREADY_IN_USE" -> "Email is already registered."
            "ERROR_WEAK_PASSWORD" -> "The account password does not meet the configured password requirements. Contact an administrator."
            "ERROR_PROVIDER_ALREADY_LINKED" -> "This session already has a sign-in account. Please restart registration."
            "ERROR_INVALID_API_KEY", "ERROR_APP_NOT_AUTHORIZED", "ERROR_INVALID_APP_CREDENTIAL" -> "The app's Firebase configuration could not be verified. Contact an administrator."
            "ERROR_INTERNAL_ERROR" -> "The sign-in service could not complete the request. Try again; contact an administrator if it continues."
            "ERROR_USER_DISABLED" -> "This sign-in account is disabled. Contact an administrator."
            "ERROR_TOO_MANY_REQUESTS" -> "Too many attempts. Please try again later."
            "NETWORK_ERROR", "UNAVAILABLE" -> "Check your connection and try again."
            else -> "Unable to complete this step. Contact an administrator if it continues."
        }
        return "$detail (code: $code)"
    }

    private fun persistTeacherSession(teacherDocument: DocumentSnapshot, teacherEmail: String) {
        val teacherId = teacherDocument.getString("teacherId")
            ?: teacherDocument.getString("instructorId")
            ?: ""
        val teacherName = teacherDocument.getString("name") ?: "Teacher"
        val teacherDepartment = teacherDocument.getString("department") ?: ""
        val teacherSchool = teacherDocument.getString("school") ?: ""
        val teacherPosition = teacherDocument.getString("position") ?: teacherDepartment
        val teacherRole = teacherDocument.getString("role") ?: "teacher"

        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            .edit()
            .putString("teacher_id", teacherId)
            .putString("teacher_name", teacherName)
            .putString("teacher_email", teacherEmail)
            .putString("teacher_department", teacherDepartment)
            .putString("teacher_school", teacherSchool)
            .putString("teacher_position", teacherPosition)
            .putString("teacher_role", teacherRole)
            .apply()
    }

    private fun generateTeacherId(onReady: (String) -> Unit) {
        val year = Calendar.getInstance().get(Calendar.YEAR).toString()
        val prefix = "T$year-"
        firestore.collection("users")
            .whereGreaterThanOrEqualTo("teacherId", "${prefix}000")
            .whereLessThanOrEqualTo("teacherId", "${prefix}999")
            .orderBy("teacherId", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { snapshot ->
                val lastTeacherId = snapshot.documents.firstOrNull()?.getString("teacherId")
                val lastSeries = lastTeacherId
                    ?.substringAfter("-")
                    ?.toIntOrNull()
                    ?: 0
                val nextSeries = (lastSeries + 1).coerceAtLeast(1)
                onReady("$prefix${nextSeries.toString().padStart(3, '0')}")
            }
            .addOnFailureListener { error ->
                _isLoading.value = false
                _errorMessage.value = failureMessage("teacher_id_generation", error)
            }
    }

    private fun deriveDefaultPasswordFromTeacherId(teacherId: String): String {
        val yearPart = teacherId.removePrefix("T").substringBefore("-")
        val shortYear = yearPart.takeLast(2)
        val series = teacherId.substringAfter("-")
        return "T$shortYear$series"
    }
}
