package com.odoo.dh_android_tire_retread_production.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.odoo.dh_android_tire_retread_production.AppContainer
import com.odoo.dh_android_tire_retread_production.R
import com.odoo.dh_android_tire_retread_production.ui.theme.Dh_android_tire_retread_productionTheme
import com.odoo.dh_android_tire_retread_production.ui.viewmodel.LoginUiState
import com.odoo.dh_android_tire_retread_production.ui.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    container: AppContainer,
    onLoginSuccess: () -> Unit
) {
    val viewModel: LoginViewModel = viewModel(factory = object : androidx.lifecycle.ViewModelProvider.Factory {
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            return LoginViewModel(container.workorderRepository, container.sessionManager) as T
        }
    })

    val uiState by viewModel.uiState.collectAsState()

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var deviceName by remember { mutableStateOf("Tablet-01") }
    var stationCode by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(uiState) {
        if (uiState is LoginUiState.Success) {
            onLoginSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.secondary
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 32.dp, vertical = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Image(
                    painter = painterResource(id = R.drawable.app_logo),
                    contentDescription = null,
                    modifier = Modifier.size(120.dp),
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Tire Retread Production",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
                Text(
                    text = "PT. Paderona Arthajaya",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.Gray,
                        letterSpacing = 1.sp
                    )
                )

                Spacer(modifier = Modifier.height(40.dp))

                // Input Fields
                LoginTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = "Username",
                    icon = Icons.Default.Person
                )
                Spacer(modifier = Modifier.height(16.dp))

                LoginTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Password",
                    icon = Icons.Default.Lock,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null
                            )
                        }
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    Box(modifier = Modifier.weight(1f)) {
                        LoginTextField(
                            value = deviceName,
                            onValueChange = { deviceName = it },
                            label = "Device Name",
                            icon = Icons.Default.TabletAndroid
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Box(modifier = Modifier.weight(1f)) {
                        LoginTextField(
                            value = stationCode,
                            onValueChange = { stationCode = it },
                            label = "Station Code",
                            icon = Icons.Default.QrCode
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Error Message
                if (uiState is LoginUiState.Error) {
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Error,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                (uiState as LoginUiState.Error).message,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Login Button
                Button(
                    onClick = { viewModel.login(username, password, deviceName, stationCode) },
                    enabled = uiState !is LoginUiState.Loading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    if (uiState is LoginUiState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            "SIGN IN",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.25.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LoginTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = Color.LightGray,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = Color.Gray
        )
    )
}
