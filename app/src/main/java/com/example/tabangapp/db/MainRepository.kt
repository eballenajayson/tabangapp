package com.example.tabangapp.db

import androidx.lifecycle.LiveData

class MainRepository(private val mainDao: MainDao) {
    suspend fun updateUserLoggedInAs(username: String, loggedInAs: String) {
        mainDao.updateUserLoggedInAs(username, loggedInAs)
    }

    suspend fun updateUserLoginStatus(username: String, loggedInAs: String, isLoggedIn: Boolean) {
        mainDao.updateUserLoginStatus(username, loggedInAs, isLoggedIn)
    }

    suspend fun getUserLastUser(): User? {
        return mainDao.getUserLastUser()
    }

    suspend fun insertReports(reports: List<Report>) {
        mainDao.insertReports(reports)
    }

    suspend fun getAllReports(): List<Report> {
        return mainDao.getAllReports()
    }

    suspend fun insertReport(report: Report) {
        mainDao.insert(report)
    }

    suspend fun getReportsForUser(userId: Int): List<Report> {
        return mainDao.getReportsForUser(userId)
    }

    val readAllData: LiveData<List<User>> = mainDao.getAllUser()

    suspend fun addUser(user: User){
        return mainDao.addUser(user)
    }

    suspend fun getUserByUsername(username: String): User? {
        return mainDao.getUserByUsername(username)
    }
}