package com.example.tabangapp.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MainDao {
    @Query("SELECT * FROM reports WHERE dateCreated BETWEEN :startOfDay AND :endOfDay")
    suspend fun getAllReports(startOfDay: Long, endOfDay: Long): List<Report>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(report: Report)

    @Query("SELECT * FROM reports WHERE userId = :userId ORDER BY id DESC")
    suspend fun getReportsForUser(userId: Int): List<Report>

    @Query("SELECT * FROM users")
    fun getAllUser(): LiveData<List<User>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addUser(user: User)

    @Query("SELECT * FROM users WHERE id = :id")
    fun getUser(id: Int): LiveData<List<User>>

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?
}
