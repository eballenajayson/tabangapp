package com.example.tabangapp.screens.start

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.tabangapp.R
import com.example.tabangapp.db.MainViewModel
import com.example.tabangapp.ui.theme.PurpleGrey40
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun Start(
    navController: NavController,
    mainViewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    var selectedIndex by remember { mutableIntStateOf(0) }
    val options = listOf("Volunteer", "Victim")
    var username by remember { mutableStateOf("johndoe") }
    var password by remember { mutableStateOf("12345") }
    var passwordVisible by remember { mutableStateOf(false) }
    val isLoading = mainViewModel.isLoading.value
    val loginSuccess = mainViewModel.loginSuccess.value
    val errorMessage = mainViewModel.errorMessage.value
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    val currentUser = mainViewModel.currentUser

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (granted) {
                coroutineScope.launch {
                    try {
                        val location = fusedLocationClient.lastLocation.await()
                        location?.let {
                            mainViewModel.updateUserLocation(
                                location = LatLng(it.latitude, it.longitude)
                            )
                        }
                    } catch (e: Exception) {
                        // handle error
                    }
                }
            } else {
                // permission denied
            }
        }
    )

    LaunchedEffect(Unit, currentUser) {
        val permissionCheck = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            coroutineScope.launch {
                try {
                    val location = fusedLocationClient.lastLocation.await()
                    location?.let {
                        mainViewModel.updateUserLocation(
                            location = LatLng(it.latitude, it.longitude)
                        )
                    }
                } catch (e: Exception) {
                    // handle error
                }
            }
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    Scaffold (
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
            Column(
                modifier = modifier.padding(30.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(R.drawable.logo),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .size(150.dp),
                    contentScale = ContentScale.Crop,
                )
                Text(
                    text = "TABANG APP",
                    fontSize = 30.sp
                )
                OutlinedTextField(
                    value = username,
                    readOnly = isLoading,
                    onValueChange = { username = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Username") },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp)
                )
                OutlinedTextField(
                    value = password,
                    readOnly = isLoading,
                    onValueChange = { password = it },
                    modifier = modifier.fillMaxWidth(),
                    label = { Text("Password") },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    visualTransformation = if (passwordVisible)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (passwordVisible)
                            Icons.Filled.Visibility
                        else
                            Icons.Filled.VisibilityOff

                        IconButton(
                            enabled = !isLoading,
                            onClick = { passwordVisible = !passwordVisible }
                        ) {
                            Icon(
                                imageVector = image,
                                contentDescription = "Toggle password visibility"
                            )
                        }
                    }
                )
                SingleChoiceSegmentedButtonRow(
                    modifier = modifier.fillMaxWidth(),
                ) {
                    options.forEachIndexed { index, label ->
                        val shape = when (index) {
                            0 -> RoundedCornerShape(
                                topStart = 8.dp,
                                bottomStart = 8.dp
                            )

                            options.lastIndex -> RoundedCornerShape(
                                topEnd = 8.dp,
                                bottomEnd = 8.dp
                            )

                            else -> RoundedCornerShape(0.dp)
                        }
                        SegmentedButton(
                            colors = SegmentedButtonDefaults.colors(
                                activeContainerColor = PurpleGrey40,
                                activeContentColor = Color.White,
                                inactiveContainerColor = Color.White,
                                inactiveContentColor = Color.Black,
                                activeBorderColor = PurpleGrey40
                            ),
                            shape = shape,
                            onClick = { selectedIndex = index },
                            selected = index == selectedIndex,
                            label = { Text(label) }
                        )
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
                        mainViewModel.login(username, password)
                    }
                ) {
                    if (isLoading) {
                        androidx.compose.material3.CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Login")
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Don't have an account?",
                        fontSize = 12.sp
                    )
                    Text(
                        text = "Register",
                        fontSize = 12.sp,
                        textDecoration = TextDecoration.Underline,
                        color = Color.Black,
                        modifier = modifier.clickable {
                            navController.navigate("register")
                        }
                    )
                }
            }
        }
    }
    LaunchedEffect(loginSuccess, errorMessage) {
        if (errorMessage != null) {
            snackbarHostState.showSnackbar(errorMessage)
            mainViewModel.resetLoginState()
        }

        if (loginSuccess) {
            val destination = if (selectedIndex == 0) "volunteer" else "victim"
            navController.navigate(destination)
            mainViewModel.resetLoginState()
        }
    }
}