package com.example.tabangapp.screens.volunteer



import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.tabangapp.components.CustomGoogleMap
import com.example.tabangapp.components.LogoutConfirmationDialog
import com.example.tabangapp.db.MainViewModel
import com.example.tabangapp.db.Report
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.google.android.gms.maps.model.LatLng


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Volunteer(
    navController: NavController,
    mainViewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val logoutMessage = mainViewModel.logoutMessage.value
    val logoutSuccess = mainViewModel.logoutSuccess.value
    val isLoading = mainViewModel.isLoading.value
    var reportLocations by remember { mutableStateOf<List<Report>>(emptyList()) }
    val showLogoutDialog = remember { mutableStateOf(false) }
    val userLocation = mainViewModel.userLocation.value
    val currentUser = mainViewModel.currentUser.value
    val refreshMessage = mainViewModel.refreshMessage.value

    LogoutConfirmationDialog(
        showDialog = showLogoutDialog,
        onLogoutConfirmed = { mainViewModel.logout() }
    )

    LaunchedEffect(currentUser) {
        if(currentUser!= null){
            mainViewModel.fetchAllReports()
            mainViewModel.getAllReports{ reports ->
                reportLocations = reports
            }
        }
    }

    LaunchedEffect(isLoading, refreshMessage) {
        if(refreshMessage != null) {
            snackbarHostState.showSnackbar(refreshMessage)
        }
        mainViewModel.getAllReports{ reports ->
            reportLocations = reports
        }
    }

    Scaffold (
        topBar = {
            Surface(
                shadowElevation = 8.dp,
                tonalElevation = 4.dp,
            ) {
                TopAppBar(
                    title = { Text("Hello Volunteer") },
                    navigationIcon = {},
                    actions = {
                        IconButton(
                            enabled = !isLoading,
                            onClick = {
                                showLogoutDialog.value = true
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Logout,
                                contentDescription = "Localized description"
                            )
                        }
                    }
                )
            }
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
            )
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            CustomGoogleMap(
                modifier = modifier,
                userLocation = userLocation,
                showMyLocation = true,
                reportLocations = reportLocations,
                mainViewModel = mainViewModel,
            )
        }
    }

    LaunchedEffect(logoutSuccess, logoutMessage) {
        if (logoutMessage != null) {
            snackbarHostState.showSnackbar(logoutMessage)
        }
        if (logoutSuccess) {
            navController.navigate("start") {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }
            mainViewModel.resetLogoutState()
        }
    }
}