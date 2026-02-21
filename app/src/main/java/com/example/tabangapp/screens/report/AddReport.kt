package com.example.tabangapp.screens.report


import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShareLocation
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.tabangapp.db.MainViewModel
import coil.compose.rememberAsyncImagePainter
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import android.Manifest
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.core.net.toUri
import com.example.tabangapp.R
import com.example.tabangapp.db.Report
import com.example.tabangapp.ui.theme.Purple40
import com.example.tabangapp.ui.theme.PurpleGrey40
import com.google.android.gms.location.Priority
import java.io.File

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReport(
    navController: NavController,
    mainViewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser = mainViewModel.currentUser
    var details by remember { mutableStateOf("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val isLoading = mainViewModel.isLoading.value
    val longitude = mainViewModel.longitude.value
    val latitude = mainViewModel.latitude.value
    val isReportInserted = mainViewModel.isReportInserted.value
    val insertReportSuccessMessage = mainViewModel.insertReportSuccessMessage.value
    val errorMessage = mainViewModel.errorMessage.value
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val inputStream = context.contentResolver.openInputStream(uri)
            val file = File(context.filesDir, "report_${System.currentTimeMillis()}.jpg")
            inputStream?.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            imageUri = file.toUri()
        }
    }

    LaunchedEffect(isReportInserted, insertReportSuccessMessage) {
        if (isReportInserted && insertReportSuccessMessage!=null) {
            snackbarHostState.showSnackbar(insertReportSuccessMessage)
            mainViewModel.resetInsertReportState()
            navController.navigate("victim")
        }
    }

    LaunchedEffect(errorMessage) {
        if (errorMessage != null) {
            snackbarHostState.showSnackbar(errorMessage)
            mainViewModel.resetError()
        }
    }

    Scaffold(
        topBar = {
            Surface(
                shadowElevation = 8.dp,
                tonalElevation = 4.dp,
            ) {
                TopAppBar(
                    title = { Text("") },
                    navigationIcon = {
                        IconButton(
                            enabled = !isLoading,
                            onClick = {
                                navController.popBackStack()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                )
            }
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState
            )
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = modifier.padding(top = 20.dp, start = 20.dp, end = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                currentUser?.fullName?.let {
                    OutlinedTextField(
                        value = it,
                        readOnly = true,
                        onValueChange = {},
                        label = { Text("Full Name") },
                        modifier = modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )
                }
                currentUser?.phoneNumber?.let {
                    OutlinedTextField(
                        value = it,
                        readOnly = true,
                        onValueChange = {},
                        label = { Text("Phone Number") },
                        modifier = modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )
                }
                OutlinedTextField(
                    value = details,
                    onValueChange = { details = it },
                    modifier = modifier.fillMaxWidth(),
                    label = { Text("Details") },
                    singleLine = false,
                    maxLines = 10,
                    minLines = 5,
                    shape = RoundedCornerShape(8.dp),
                    readOnly = isLoading
                )
                Row(
                    modifier = modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = longitude,
                        onValueChange = { mainViewModel.updateLongitude(it) },
                        modifier = modifier.weight(1f),
                        label = { Text("Longitude") },
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        readOnly = isLoading,
                        isError = longitude.isBlank()
                    )
                    OutlinedTextField(
                        value = latitude,
                        onValueChange = { mainViewModel.updateLatitude(it) },
                        modifier = modifier.weight(1f),
                        label = { Text("Latitude") },
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        readOnly = isLoading,
                        isError = latitude.isBlank()
                    )
                    ElevatedButton(
                        modifier = modifier
                            .fillMaxHeight(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = PurpleGrey40,
                            contentColor = Color.White
                        ),
                        enabled = !isLoading,
                        onClick = {
                            val permissionCheck = ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            )
                            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                                coroutineScope.launch {
                                    try {
                                        val location = fusedLocationClient
                                            .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                                            .await()

                                        if (location != null) {
                                            mainViewModel.updateLongitude(location.longitude.toString())
                                            mainViewModel.updateLatitude(location.latitude.toString())
                                            Log.d("TABANGAPP_LOG", "${location.longitude} | ${location.latitude}")
                                        }
                                    } catch (e: Exception) {
                                        // handle error
                                        Log.d("TABANGAPP_LOG", e.toString())
                                    }
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShareLocation,
                            contentDescription = "Get Current Location"
                        )
                    }
                }
                Column(
                    modifier = modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(imageUri?:R.drawable.no_image),
                        contentDescription = "Selected Image",
                        modifier = modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentScale = ContentScale.Fit,
                    )
                    OutlinedButton(
                        modifier = modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.Black
                        ),
                        enabled = !isLoading,
                        onClick = {
                            imagePickerLauncher.launch("image/*")
                        }
                    ) {
                        Text("Upload Image")
                    }
                }
                ElevatedButton(
                    modifier = modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = PurpleGrey40,
                        contentColor = Color.White
                    ),
                    onClick = {
                        if (
                            longitude.isNotBlank() &&
                            latitude.isNotBlank() &&
                            currentUser != null
                        ){
                            val report = Report(
                                userId = currentUser.id,
                                fullName = currentUser.fullName,
                                phoneNumber = currentUser.fullName,
                                details = details,
                                longitude = longitude,
                                latitude = latitude,
                                imageUri = imageUri.toString()
                            )
                            mainViewModel.apiAddReport(context, report, imageUri)
                        }
                    }
                ){
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 1.dp,
                        )
                    } else {
                        Text("SUBMIT REPORT")
                    }
                }
            }
        }
    }
}

