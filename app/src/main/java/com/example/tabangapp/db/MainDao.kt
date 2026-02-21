package com.example.tabangapp.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface MainDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertReports(reports: List<Report>)

    @Query("""
    SELECT * FROM reports
    WHERE replace(dateCreated, 'T', ' ') BETWEEN 
        datetime('now','localtime','start of day')
        AND 
        datetime('now','localtime','start of day','+1 day','-1 second')
    ORDER BY dateCreated DESC
""")
    suspend fun getAllReports(): List<Report>

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
