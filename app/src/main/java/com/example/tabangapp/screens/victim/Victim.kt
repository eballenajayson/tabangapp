package com.example.tabangapp.screens.victim


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.tabangapp.db.MainViewModel
import com.example.tabangapp.db.Report
import androidx.core.net.toUri
import com.example.tabangapp.components.LogoutConfirmationDialog
import com.example.tabangapp.ui.theme.PurpleGrey40
import com.example.tabangapp.ui.theme.Red
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Victim(
    navController: NavController,
    mainViewModel: MainViewModel,
    modifier: Modifier = Modifier,
) {
    val logoutMessage = mainViewModel.logoutMessage.value
    val logoutSuccess = mainViewModel.logoutSuccess.value
    var userReports by remember { mutableStateOf<List<Report>>(emptyList()) }
    val snackbarHostState = remember { SnackbarHostState() }
    val showLogoutDialog = remember { mutableStateOf(false) }
    val currentUser = mainViewModel.currentUser.value
    val reports = mainViewModel.reports

    LogoutConfirmationDialog(
        showDialog = showLogoutDialog,
        onLogoutConfirmed = { mainViewModel.logout() }
    )

    LaunchedEffect(currentUser, reports, Unit) {
        mainViewModel.fetchAllReports()
        if(currentUser!= null){
            mainViewModel.getReportsForCurrentUser { reports ->
                userReports = reports
            }
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

    Scaffold(
        topBar = {
            Surface(
                shadowElevation = 8.dp,
                tonalElevation = 4.dp,
            ) {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White,
                        titleContentColor = Color.Black
                    ),
                    title = { Text("How can we help?") },
                    navigationIcon = {},
                    actions = {
                        IconButton(
                            onClick = {
                                showLogoutDialog.value = true
                            }
                        ) {
                            Icon(
                                tint = Red,
                                imageVector = Icons.Filled.Logout,
                                contentDescription = "Localized description"
                            )
                        }
                    },
                )
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = modifier.padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = modifier.fillMaxSize()
                ) {
                    items(userReports) { report ->
                        val formattedDate = remember(report.dateCreated) {
                            report.dateCreated?.let {
                                LocalDateTime.parse(it)
                                    .format(DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a"))
                            } ?: ""
                        }
                        Card(
                            shape = RoundedCornerShape(8.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            modifier = modifier
                                .fillMaxWidth()
                                .padding(top = 20.dp, bottom = 15.dp)
                        ) {
                            Column(modifier = modifier.padding(16.dp)) {
                                report.imageUri?.let { uriString ->
                                    val uri = uriString.toUri()
//                                    val painter = rememberAsyncImagePainter(
//                                        model = "http://10.0.2.2:8000${uri}"
//                                    )
                                    val painter = rememberAsyncImagePainter(
                                        model = "https://tabangapi-fastapi-production.up.railway.app${uri}"
                                    )
                                    Image(
                                        painter = painter,
                                        contentDescription = "Report Image",
                                        modifier = modifier
                                            .fillMaxWidth()
                                            .height(150.dp)
                                            .padding(bottom = 10.dp),
                                        contentScale = ContentScale.Fit
                                    )
                                }
                                Text(text = "${report.imageUri}")
                                Text(text = report.fullName)
                                Text(text = report.phoneNumber)
                                Text(text = report.details)
                                Text(text = "Longitude: ${report.longitude}")
                                Text(text = "Latitude: ${report.latitude}")
                                Text(text = "Date: $formattedDate")
                            }
                        }
                    }
                }
            }
            FloatingActionButton(
                containerColor = PurpleGrey40,
                contentColor = Color.White,
                onClick = {
                    navController.navigate("add-report")
                },
                modifier = modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Report"
                )
            }
        }
    }
}
