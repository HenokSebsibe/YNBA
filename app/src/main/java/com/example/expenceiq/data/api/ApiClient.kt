package com.example.expenceiq.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Singleton object to manage Retrofit API client.
 */
object ApiClient {
    // 10.0.2.2 is the standard IP used by the Android emulator to access the host's localhost.
    private const val BASE_URL = "http://10.0.2.2:3000/"

    // Logging interceptor to debug API requests and responses in the Logcat
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // OkHttpClient with logging enabled
    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    // Retrofit instance configuration
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(httpClient)
        .build()

    // Public service instance to be used throughout the app
    val apiService: ApiService = retrofit.create(ApiService::class.java)
}
