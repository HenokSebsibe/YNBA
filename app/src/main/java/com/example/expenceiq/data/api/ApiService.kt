package com.example.expenceiq.data.api

import com.example.expenceiq.data.model.DashboardResponse
import com.example.expenceiq.data.model.LoginRequest
import com.example.expenceiq.data.model.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("api/dashboard/summary")
    suspend fun getDashboardSummary(@Header("Authorization") token: String): Response<DashboardResponse>

    // Add other endpoints as needed
    // @GET("api/expenses")
    // suspend fun getExpenses(@Header("Authorization") token: String): Response<ExpenseResponse>
}
