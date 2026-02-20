package com.example.tabangapp.api

import com.example.tabangapp.db.Report
import com.example.tabangapp.db.User
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @POST("/api/v1/register")
    suspend fun register(@Body user: User): User

    @POST("/api/v1/reports")
    suspend fun addReport(@Body report: Report): Report

    @GET("/api/v1/reports")
    suspend fun getAllReports(): List<Report>

    @GET("/api/v1/users/{userId}/reports")
    suspend fun getReportsByUser(@Path("userId") userId: Int): List<Report>
}
