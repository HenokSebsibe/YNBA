package com.example.expenceiq.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.expenceiq.data.api.ApiClient
import com.example.expenceiq.data.model.LoginRequest
import com.example.expenceiq.utils.SessionManager
import kotlinx.coroutines.launch

class LoginActivity : ComponentActivity() {

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionManager = SessionManager(this)

        // Check if user is already logged in and redirect based on role
        val savedToken = sessionManager.fetchAuthToken()
        Log.d("ROLE_DEBUG", "Saved token exists = ${savedToken != null}")
        if (savedToken != null) {
            val savedRole = sessionManager.getUserRole()?.trim() ?: "USER"
            Log.d("ROLE_DEBUG", "Saved role = '$savedRole'")
            handleNavigation(savedRole)
        }

        setContent {
            LoginScreen { email, password ->
                performLogin(email, password)
            }
        }
    }

    private fun performLogin(email: String, password: String) {
        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.login(LoginRequest(email, password))
                if (response.isSuccessful && response.body()?.success == true) {
                    val loginResponse = response.body()
                    if (loginResponse != null && loginResponse.token != null && loginResponse.user != null) {
                        // Store role and token (normalize role value)
                        val normalizedRole = loginResponse.user.role_name.trim()
                        Log.d("ROLE_DEBUG", "Server role_name = '${loginResponse.user.role_name}'")
                        Log.d("ROLE_DEBUG", "Server role_id = ${loginResponse.user.role_id}")
                        Log.d("ROLE_DEBUG", "Server token exists = ${loginResponse.token != null}")
                        Log.d("ROLE_DEBUG", "Normalized role = '$normalizedRole'")
                        sessionManager.saveAuthToken(loginResponse.token)
                        sessionManager.saveRole(normalizedRole)
                        sessionManager.saveUserDetails(
                            loginResponse.user.id,
                            loginResponse.user.email ?: "",
                            loginResponse.user.username ?: "",
                            loginResponse.user.company_id,
                            normalizedRole
                        )
                        // Log saved values to verify SessionManager persistence
                        Log.d("ROLE_DEBUG", "Saved role = '${sessionManager.getRole()}'")
                        Log.d("ROLE_DEBUG", "Saved token exists = ${sessionManager.fetchAuthToken() != null}")
                        handleNavigation(normalizedRole)
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "Login Failed", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@LoginActivity, "Network Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleNavigation(role: String) {
        if (role.trim().uppercase() == "ADMIN") {
            Log.d("ROLE_DEBUG", "About to open AdminDashboardActivity")
            startActivity(Intent(this, AdminDashboardActivity::class.java))
        } else {
            Log.d("ROLE_DEBUG", "About to open DashboardActivity")
            startActivity(Intent(this, DashboardActivity::class.java))
        }
        finish()
    }
}

@Composable
fun LoginScreen(onLoginClick: (String, String) -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "ExpenseIQ", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { onLoginClick(email, password) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }
    }
}
