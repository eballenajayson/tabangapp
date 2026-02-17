package com.example.tabangapp.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val username: String,
    val password: String,
    val fullName: String,
    val phoneNumber: String
)

@Entity(tableName = "reports")
data class Report(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val fullName: String,
    val phoneNumber: String,
    val details: String,
    val longitude: String,
    val latitude: String,
    val imageUri: String? = null,
    val dateCreated: Long = System.currentTimeMillis()
)
