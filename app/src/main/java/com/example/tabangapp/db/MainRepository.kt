package com.example.tabangapp.db

import androidx.lifecycle.LiveData

class MainRepository(private val mainDao: MainDao) {
    suspend fun getAllReports(startOfDay: Long, endOfDay: Long): List<Report> {
        return mainDao.getAllReports(startOfDay = startOfDay, endOfDay = endOfDay)
    }

    suspend fun insertReport(report: Report) {
        mainDao.insert(report)
    }

    suspend fun getReportsForUser(userId: Int): List<Report> {
        return mainDao.getReportsForUser(userId)
    }

    val readAllData: LiveData<List<User>> = mainDao.getAllUser()

    suspend fun addUser(user: User){
        mainDao.addUser(user)
    }

    suspend fun getUserByUsername(username: String): User? {
        return mainDao.getUserByUsername(username)
    }
}