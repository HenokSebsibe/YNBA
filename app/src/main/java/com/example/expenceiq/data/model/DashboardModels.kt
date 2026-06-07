package com.example.expenceiq.data.model

data class DashboardResponse(
    val success: Boolean,
    val data: DashboardData
)

data class DashboardData(
    val total_income: Double,
    val total_expenses: Double,
    val balance: Double,
    val recent_transactions: List<Transaction>,
    val user_count: Int?,
    val role: String
)

data class Transaction(
    val id: Int,
    val name: String,
    val amount: Double,
    val date: String,
    val type: String // "INCOME" or "EXPENSE"
)
