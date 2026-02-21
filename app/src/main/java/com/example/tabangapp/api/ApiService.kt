package com.example.tabangapp.api

import com.example.tabangapp.db.Report
import com.example.tabangapp.db.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {
    @POST("/api/v1/register")
    suspend fun register(@Body user: User): User

//    @POST("/api/v1/reports")
//    suspend fun addReport(@Body report: Report): Report

    @Multipart
    @POST("/api/v1/reports")
    suspend fun addReport(
        @Part("userId") userId: RequestBody,
        @Part("fullName") fullName: RequestBody,
        @Part("phoneNumber") phoneNumber: RequestBody,
        @Part("details") details: RequestBody,
        @Part("longitude") longitude: RequestBody,
        @Part("latitude") latitude: RequestBody,
        @Part image: MultipartBody.Part?
    ): Report


    @GET("/api/v1/reports")
    suspend fun getAllReports(): List<Report>

    @GET("/api/v1/users/{userId}/reports")
    suspend fun getReportsByUser(@Path("userId") userId: Int): List<Report>
}
