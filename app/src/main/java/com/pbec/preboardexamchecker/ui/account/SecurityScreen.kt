package com.pbec.preboardexamchecker.ui.account

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.pbec.preboardexamchecker.ui.theme.BrandTopAppBar
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityScreen(
    navController: NavController,
    viewModel: SecurityViewModel = hiltViewModel()
) {
    var showChangePasswordSheet by remember { mutableStateOf(false) }
    var showChangeEmailSheet by remember { mutableStateOf(false) }
    val emailState by viewModel.emailState.collectAsState()
    var showLoginActivitySheet by remember { mutableStateOf(false) }
    
    val sheetState = rememberModalBottomSheetState()
    val snackbarHostState = remember { SnackbarHostState() }
    val isUpdatingPassword by viewModel.isUpdatingPassword.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner, viewModel) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) viewModel.refreshPendingEmailStatus()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is SecurityViewModel.UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    Scaffold(
        // Outer MainActivity Scaffold already reserves the bottom-nav inset; zero here to avoid doubling.
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            BrandTopAppBar(
                title = "Security",
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            SectionHeader("Account security")
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column {
                    SecurityMenuItem(
                        icon = Icons.Default.Lock,
                        title = "Change Password",
                        subtitle = "Update your account password",
                        onClick = { showChangePasswordSheet = true }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                    SecurityMenuItem(
                        icon = Icons.Default.Email,
                        title = "Change Email Address",
                        subtitle = emailState.pendingEmail?.let { "Verification pending for $it" }
                            ?: "Update your registered email address.",
                        onClick = {
                            showChangeEmailSheet = true
                            viewModel.refreshPendingEmailStatus()
                        }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                    SecurityMenuItem(
                        icon = Icons.Default.Key,
                        title = "Login Activity",
                        subtitle = "View recent login sessions",
                        onClick = { showLoginActivitySheet = true }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Security Tips Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Security Tips",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    SecurityTipItem("Use a strong, unique password")
                    SecurityTipItem("Enable two-factor authentication")
                    SecurityTipItem("Never share your login credentials")
                    SecurityTipItem("Log out from shared devices")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        if (showChangePasswordSheet) {
            ModalBottomSheet(
                onDismissRequest = { showChangePasswordSheet = false },
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                ChangePasswordSheetContent(
                    onDismiss = { showChangePasswordSheet = false },
                    isUpdating = isUpdatingPassword,
                    onSubmit = { currentPassword, newPassword, confirmPassword ->
                        viewModel.changePassword(
                            currentPassword = currentPassword,
                            newPassword = newPassword,
                            confirmPassword = confirmPassword,
                            onSuccess = { showChangePasswordSheet = false }
                        )
                    }
                )
            }
        }

        if (showChangeEmailSheet) {
            ModalBottomSheet(
                onDismissRequest = { if (!emailState.busy) showChangeEmailSheet = false },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ) {
                ChangeEmailSheetContent(emailState,
                    onSubmit = viewModel::requestEmailChange,
                    onFieldEdit = viewModel::clearEmailFieldError,
                    onCheck = viewModel::checkEmailStatus,
                    onDismiss = { showChangeEmailSheet = false })
            }
        }

        if (showLoginActivitySheet) {
            ModalBottomSheet(
                onDismissRequest = { showLoginActivitySheet = false },
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                LoginActivitySheetContent()
            }
        }
    }
}

@Composable
private fun ChangeEmailSheetContent(
    state: EmailChangeState,
    onSubmit: (String, String, () -> Unit) -> Unit,
    onFieldEdit: (EmailField) -> Unit,
    onCheck: () -> Unit,
    onDismiss: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var editRequest by remember { mutableStateOf(false) }
    val pending = state.pendingEmail
    val emailFocus = remember { FocusRequester() }
    val passwordFocus = remember { FocusRequester() }
    LaunchedEffect(state.validationAttempt) {
        if (!state.needsProfileSync && (pending == null || editRequest)) {
            when (state.invalidField) {
                EmailField.EMAIL -> emailFocus.requestFocus()
                EmailField.PASSWORD -> passwordFocus.requestFocus()
                null -> Unit
            }
        }
    }
    Column(Modifier.fillMaxWidth().imePadding().verticalScroll(rememberScrollState())
        .padding(24.dp).navigationBarsPadding(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Change Email Address", style = MaterialTheme.typography.headlineSmall)
        Text("Current email", style = MaterialTheme.typography.labelLarge)
        Text(state.currentEmail.ifBlank { "Sign in again to view your email." })
        if (state.needsProfileSync) {
            Text("Finish updating your account", style = MaterialTheme.typography.titleLarge)
            Text("Check verification status to sync your account before requesting another email change.")
        } else if (pending != null && !editRequest) {
            if (pending.equals(state.currentEmail, ignoreCase = true)) {
                Text("Email verified", style = MaterialTheme.typography.titleLarge)
                Text("Your sign-in email has changed. Check verification status to finish syncing your account profile.")
            } else {
                Text("Check your new email", style = MaterialTheme.typography.titleLarge)
                Text("We sent a verification link to $pending. Open it to confirm your new address, then return here.")
                Text("Your email has not changed yet.")
            }
        } else {
            Text("We’ll send a verification link to your new email address. Your current email stays active until you verify the new one.")
            OutlinedTextField(value = email, onValueChange = { email = it; onFieldEdit(EmailField.EMAIL) },
                label = { Text("New email address") }, singleLine = true, enabled = !state.busy,
                isError = state.emailError != null,
                supportingText = { state.emailError?.let { Text(it) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), modifier = Modifier.fillMaxWidth().focusRequester(emailFocus))
            OutlinedTextField(value = password, onValueChange = { password = it; onFieldEdit(EmailField.PASSWORD) },
                label = { Text("Current password") }, singleLine = true, enabled = !state.busy,
                isError = state.passwordError != null,
                supportingText = { state.passwordError?.let { Text(it) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = { IconButton(onClick = { passwordVisible = !passwordVisible }, enabled = !state.busy) {
                    Icon(if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password")
                } }, modifier = Modifier.fillMaxWidth().focusRequester(passwordFocus))
            Button(onClick = { onSubmit(email, password) { password = ""; editRequest = false } },
                enabled = !state.busy, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                Text(if (state.action == EmailAction.SEND_LINK) "Sending link…" else "Send verification link")
            }
        }
        state.message?.let { Text(it, modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }) }
        if (pending != null || state.needsProfileSync) {
            OutlinedButton(onClick = onCheck, enabled = !state.busy, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                Text(if (state.action == EmailAction.CHECK_STATUS) "Checking…" else "Check verification status")
            }
        }
        if (pending != null && !editRequest && !state.needsProfileSync) {
            TextButton(onClick = { editRequest = true }, enabled = !state.busy) { Text("Use a different email") }
        }
        TextButton(onClick = onDismiss, enabled = !state.busy, modifier = Modifier.fillMaxWidth()) { Text("Done") }
    }
}

@Composable
fun SecurityMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun SecurityTipItem(tip: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Text("• ", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = tip, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun ChangePasswordSheetContent(
    onDismiss: () -> Unit,
    isUpdating: Boolean,
    onSubmit: (String, String, String) -> Unit
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    
    var currentPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .navigationBarsPadding()
    ) {
        Text(
            text = "Change Password",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Enter your current password and choose a new one",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))

        PasswordField(
            value = currentPassword,
            onValueChange = { currentPassword = it },
            label = "Current Password",
            isVisible = currentPasswordVisible,
            onToggleVisibility = { currentPasswordVisible = !currentPasswordVisible }
        )
        Spacer(modifier = Modifier.height(16.dp))
        PasswordField(
            value = newPassword,
            onValueChange = { newPassword = it },
            label = "New Password",
            isVisible = newPasswordVisible,
            onToggleVisibility = { newPasswordVisible = !newPasswordVisible }
        )
        Spacer(modifier = Modifier.height(16.dp))
        PasswordField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = "Confirm New Password",
            isVisible = confirmPasswordVisible,
            onToggleVisibility = { confirmPasswordVisible = !confirmPasswordVisible }
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                onSubmit(currentPassword, newPassword, confirmPassword)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            enabled = !isUpdating
        ) {
            if (isUpdating) {
                CircularProgressIndicator(
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Update Password", fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isVisible: Boolean,
    onToggleVisibility: () -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = onToggleVisibility) {
                Icon(
                    imageVector = if (isVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = null
                )
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
        )
    )
}

@Composable
fun LoginActivitySheetContent() {
    val activities = listOf(
        LoginActivityItemData("Android Phone", "Manila, Philippines", "Now", true, Icons.Default.Smartphone),
        LoginActivityItemData("Windows PC", "Quezon City, Philippines", "Yesterday, 3:45 PM", false, Icons.Default.Laptop),
        LoginActivityItemData("Android Tablet", "Makati, Philippines", "Jan 28, 2026, 10:30 AM", false, Icons.Default.TabletAndroid),
        LoginActivityItemData("MacBook Pro", "Cebu City, Philippines", "Jan 25, 2026, 2:15 PM", false, Icons.Default.Laptop)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .navigationBarsPadding()
    ) {
        Text(
            text = "Login Activity",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Recent devices that have accessed your account",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(activities) { activity ->
                LoginActivityItem(activity)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

data class LoginActivityItemData(
    val device: String,
    val location: String,
    val time: String,
    val isCurrent: Boolean,
    val icon: ImageVector
)

@Composable
fun LoginActivityItem(activity: LoginActivityItemData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (activity.isCurrent) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.surface, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = activity.icon,
                    contentDescription = null,
                    tint = if (activity.isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = activity.device,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (activity.isCurrent) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "This device",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                Text(
                    text = "${activity.location} • ${activity.time}",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
