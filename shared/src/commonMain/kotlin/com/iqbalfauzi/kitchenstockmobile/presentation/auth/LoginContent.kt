package com.iqbalfauzi.kitchenstockmobile.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iqbalfauzi.kitchenstockmobile.ui.theme.KitchenStockTheme
import kitchenstockmobile.shared.generated.resources.Res
import kitchenstockmobile.shared.generated.resources.ic_apple
import kitchenstockmobile.shared.generated.resources.ic_google
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun LoginContent(
    state: LoginScreenState,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onPasswordVisibilityToggle: () -> Unit,
    onLoginClick: () -> Unit,
    onGoogleLoginClick: () -> Unit,
    onAppleLoginClick: () -> Unit,
    onSignUpClick: () -> Unit,
    onForgotPasswordClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.primary.copy(alpha = 0.05f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(colorScheme.surface)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LoginHeader()

            Spacer(modifier = Modifier.height(32.dp))

            LoginEmailField(
                email = state.email,
                onEmailChanged = onEmailChanged
            )

            Spacer(modifier = Modifier.height(16.dp))

            LoginPasswordField(
                password = state.password,
                isPasswordVisible = state.isPasswordVisible,
                onPasswordChanged = onPasswordChanged,
                onPasswordVisibilityToggle = onPasswordVisibilityToggle
            )

            TextButton(
                onClick = onForgotPasswordClick,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(
                    text = "Forgot Password?",
                    color = colorScheme.primaryContainer,
                    style = MaterialTheme.typography.labelLarge
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            LoginButton(
                isLoading = state.isLoading,
                onClick = onLoginClick
            )

            Spacer(modifier = Modifier.height(24.dp))

            SocialLoginSection(
                isLoading = state.isLoading,
                onGoogleLoginClick = onGoogleLoginClick,
                onAppleLoginClick = onAppleLoginClick
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (state.error != null) {
                Text(
                    text = state.error,
                    color = colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            LoginFooter(onSignUpClick = onSignUpClick)
        }
    }
}

@Composable
private fun LoginHeader() {
    val colorScheme = MaterialTheme.colorScheme
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(colorScheme.primaryContainer.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Restaurant,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = colorScheme.primaryContainer
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Kitchen Stock",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = colorScheme.onSurface
        )

        Text(
            text = "Welcome back! Please enter your details.",
            style = MaterialTheme.typography.bodyMedium,
            color = colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun LoginEmailField(
    email: String,
    onEmailChanged: (String) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Email Address",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChanged,
            placeholder = { Text("Enter your email") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colorScheme.primaryContainer,
                unfocusedBorderColor = colorScheme.outline
            )
        )
    }
}

@Composable
private fun LoginPasswordField(
    password: String,
    isPasswordVisible: Boolean,
    onPasswordChanged: (String) -> Unit,
    onPasswordVisibilityToggle: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Password",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChanged,
            placeholder = { Text("........") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = onPasswordVisibilityToggle) {
                    Icon(
                        imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = null
                    )
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colorScheme.primaryContainer,
                unfocusedBorderColor = colorScheme.outline
            )
        )
    }
}

@Composable
private fun LoginButton(
    isLoading: Boolean,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = colorScheme.primaryContainer,
            contentColor = colorScheme.onPrimaryContainer
        ),
        enabled = !isLoading
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = colorScheme.onPrimaryContainer,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = "Login",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun SocialLoginSection(
    isLoading: Boolean,
    onGoogleLoginClick: () -> Unit,
    onAppleLoginClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f))
            Text(
                text = "or continue with",
                modifier = Modifier.padding(horizontal = 8.dp),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            HorizontalDivider(modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(24.dp))

        SocialLoginButton(
            text = "Google",
            icon = Res.drawable.ic_google,
            onClick = onGoogleLoginClick,
            isLoading = isLoading,
            tint = Color.Unspecified
        )

        Spacer(modifier = Modifier.height(12.dp))

        SocialLoginButton(
            text = "Apple",
            icon = Res.drawable.ic_apple,
            onClick = onAppleLoginClick,
            isLoading = isLoading,
            tint = colorScheme.onSurface
        )
    }
}

@Composable
private fun SocialLoginButton(
    text: String,
    icon: DrawableResource,
    onClick: () -> Unit,
    isLoading: Boolean,
    tint: Color = Color.Unspecified
) {
    val colorScheme = MaterialTheme.colorScheme
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = colorScheme.onSurface
        ),
        border = ButtonDefaults.outlinedButtonBorder(enabled = !isLoading).copy(
            brush = androidx.compose.ui.graphics.SolidColor(colorScheme.outline.copy(alpha = 0.5f))
        ),
        enabled = !isLoading
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = tint
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun LoginFooter(onSignUpClick: () -> Unit) {
    val colorScheme = MaterialTheme.colorScheme
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "Don't have an account?",
            style = MaterialTheme.typography.bodyMedium,
            color = colorScheme.onSurface
        )
        TextButton(onClick = onSignUpClick) {
            Text(
                text = "Sign Up",
                color = colorScheme.primaryContainer,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview
@Composable
private fun LoginContentPreview() {
    KitchenStockTheme {
        LoginContent(
            state = LoginScreenState(),
            onEmailChanged = {},
            onPasswordChanged = {},
            onPasswordVisibilityToggle = {},
            onLoginClick = {},
            onGoogleLoginClick = {},
            onAppleLoginClick = {},
            onSignUpClick = {},
            onForgotPasswordClick = {}
        )
    }
}

@Preview
@Composable
private fun LoginHeaderPreview() {
    KitchenStockTheme {
        Surface {
            LoginHeader()
        }
    }
}

@Preview
@Composable
private fun LoginEmailFieldPreview() {
    KitchenStockTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            LoginEmailField(email = "test@example.com", onEmailChanged = {})
        }
    }
}

@Preview
@Composable
private fun LoginButtonPreview() {
    KitchenStockTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            LoginButton(isLoading = false, onClick = {})
        }
    }
}

@Preview
@Composable
private fun SocialLoginSectionPreview() {
    KitchenStockTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            SocialLoginSection(
                isLoading = false,
                onGoogleLoginClick = {},
                onAppleLoginClick = {}
            )
        }
    }
}
