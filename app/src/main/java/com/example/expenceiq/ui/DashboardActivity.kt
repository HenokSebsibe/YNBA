package com.example.expenceiq.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.example.expenceiq.data.api.ApiClient
import com.example.expenceiq.data.model.DashboardData
import com.example.expenceiq.data.model.Transaction
import com.example.expenceiq.utils.SessionManager
import kotlinx.coroutines.launch
import android.util.Log

class DashboardActivity : ComponentActivity() {

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionManager = SessionManager(this)

        Log.d("ROLE_DEBUG", "DashboardActivity started")
        val savedRole = sessionManager.getRole()
        Log.d("ROLE_DEBUG", "Saved role = ${savedRole}")
        Log.d("ROLE_DEBUG", "Saved token exists = ${sessionManager.fetchAuthToken() != null}")

        // CRITICAL: Verify this is NOT an ADMIN user
        // DashboardActivity is for regular users only
        if (savedRole != null && savedRole.trim().equals("ADMIN", ignoreCase = true)) {
            Log.d("ROLE_DEBUG", "DashboardActivity - ADMIN user attempted access. Redirecting to AdminDashboardActivity.")
            val intent = Intent(this, AdminDashboardActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            return
        }

        val token = sessionManager.fetchAuthToken()
        if (token == null) {
            startLoginActivity()
            return
        }

        setContent {
            DashboardScreen(
                token = "Bearer $token",
                onLogout = {
                    sessionManager.clearSession()
                    startLoginActivity()
                }
            )
        }
    }

    private fun startLoginActivity() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    override fun onResume() {
        super.onResume()
        // On resume, verify role is still set correctly
        val savedRole = sessionManager.getRole()
        if (savedRole != null && savedRole.trim().equals("ADMIN", ignoreCase = true)) {
            Log.d("ROLE_DEBUG", "DashboardActivity - onResume: ADMIN role detected. Redirecting to AdminDashboardActivity.")
            val intent = Intent(this, AdminDashboardActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(token: String, onLogout: () -> Unit) {
    var dashboardData by remember { mutableStateOf<DashboardData?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        try {
            val response = ApiClient.apiService.getDashboardSummary(token)
            if (response.isSuccessful) {
                dashboardData = response.body()?.data
            }
        } catch (e: Exception) {
            // Handle error
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ExpenseIQ Dashboard") },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
                    }
                }
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            dashboardData?.let { data ->
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .padding(16.dp)
                        .fillMaxSize()
                ) {
                    SummaryCards(data)
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(text = "Recent Transactions", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    TransactionList(data.recent_transactions)
                }
            }
        }
    }
}

@Composable
fun SummaryCards(data: DashboardData) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        SummaryCard("Income", "$${data.total_income}", Color(0xFF4CAF50), modifier = Modifier.weight(1f))
        SummaryCard("Expenses", "$${data.total_expenses}", Color(0xFFF44336), modifier = Modifier.weight(1f))
    }
    Spacer(modifier = Modifier.height(8.dp))
    SummaryCard("Balance", "$${data.balance}", Color(0xFF2196F3), modifier = Modifier.fillMaxWidth())
}

@Composable
fun SummaryCard(title: String, amount: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, fontSize = 14.sp, color = color)
            Text(text = amount, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

@Composable
fun TransactionList(transactions: List<Transaction>) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(transactions) { transaction ->
            TransactionItem(transaction)
        }
    }
}

@Composable
fun TransactionItem(transaction: Transaction) {
    val color = if (transaction.type == "INCOME") Color(0xFF4CAF50) else Color(0xFFF44336)
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = transaction.name, fontWeight = FontWeight.Bold)
                Text(text = transaction.date, fontSize = 12.sp, color = Color.Gray)
            }
            Text(
                text = "${if (transaction.type == "INCOME") "+" else "-"}$${transaction.amount}",
                color = color,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
