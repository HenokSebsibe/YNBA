package com.example.expenceiq.data.model

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val success: Boolean,
    val token: String?,
    val user: User?
)

data class User(
    val id: Int,
    val username: String?,
    val email: String?,
    val role_id: Int,
    val role_name: String,
    val company_id: Int
)
