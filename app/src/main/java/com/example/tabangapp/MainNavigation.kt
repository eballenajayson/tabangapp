package com.example.tabangapp

import android.app.Application
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tabangapp.db.MainViewModel
import com.example.tabangapp.screens.register.Register
import com.example.tabangapp.screens.report.AddReport
import com.example.tabangapp.screens.start.Start
import com.example.tabangapp.screens.victim.Victim
import com.example.tabangapp.screens.volunteer.Volunteer

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val mainViewModel: MainViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "start"
    ) {
        composable("start") {
            Start(
                navController=navController,
                mainViewModel = mainViewModel
            )
        }

        composable("volunteer") {
            Volunteer(
                navController=navController,
                mainViewModel = mainViewModel
            )
        }

        composable("victim") {
            Victim(
                navController=navController,
                mainViewModel = mainViewModel
            )
        }

        composable("register") {
            Register(
                navController=navController,
                mainViewModel = mainViewModel)

        }

        composable("add-report") {
            AddReport(
                navController=navController,
                mainViewModel = mainViewModel)

        }
    }
}