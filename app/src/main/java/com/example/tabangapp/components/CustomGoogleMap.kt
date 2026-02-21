package com.example.tabangapp.components

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*
import com.example.tabangapp.db.Report
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import coil.compose.rememberAsyncImagePainter
import com.example.tabangapp.R
import com.example.tabangapp.db.MainViewModel
import com.example.tabangapp.ui.theme.Blue
import com.example.tabangapp.ui.theme.Purple40
import com.example.tabangapp.ui.theme.Purple80
import com.example.tabangapp.ui.theme.PurpleGrey40
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@SuppressLint("LocalContextResourcesRead")
@Composable
fun CustomGoogleMap(
    modifier: Modifier = Modifier,
    userLocation: LatLng? = null,
    showMyLocation: Boolean = false,
    reportLocations: List<Report> = emptyList(),
    mainViewModel: MainViewModel,
) {
    val defaultLocation = LatLng(14.5995, 120.9842)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 6f)
    }
    var selectedReport by remember { mutableStateOf<Report?>(null) }
    val context = LocalContext.current
    val isLoading = mainViewModel.isLoading.value


    // 🚀 Move camera when userLocation changes
    LaunchedEffect(userLocation) {
        userLocation?.let {
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(it, 10f),
                durationMs = 1000
            )
        }
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = showMyLocation
            ),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = true,
                myLocationButtonEnabled = true
            )
        ) {

            userLocation?.let {
                Marker(
                    state = MarkerState(position = it),
                    title = "You are here",
                    snippet = "Current location",
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE),
                )
            }

            reportLocations.forEach { report ->
                val lat = report.latitude.toDoubleOrNull()
                val lng = report.longitude.toDoubleOrNull()

                if (lat != null && lng != null) {
                    Marker(
                        state = MarkerState(position = LatLng(lat, lng)),
                        title = "Report",
                        snippet = "Reported Incident",
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED),
                        onClick = {
                            selectedReport = report
                            true
                        }
                    )
                }
            }
        }
        ElevatedButton (
            modifier = modifier
                .padding(top = 8.dp, start = 10.dp)
                .align(Alignment.TopStart),
            shape = RoundedCornerShape(4.dp),
            colors = ButtonDefaults.elevatedButtonColors(
                containerColor = Color.White,
                contentColor = Color.Black,
            ),
            onClick = {
                mainViewModel.fetchAllReports()
            },
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 8.dp
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(24.dp),
                    color = Purple40,
                    strokeWidth = 2.dp,
                )
            } else {
                Icon(
                    tint = Purple40,
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = "Refresh Icon"
                )
                Text("Refresh")
            }
        }

        selectedReport?.let { report ->
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.Center)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    report.imageUri?.let { uriString ->
                        val uri = uriString.toUri()
                        val painter = rememberAsyncImagePainter(
                            model = "http://10.0.2.2:8000${uri}"
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
                    Text(
                        text = "${report.imageUri}"
                    )
                    Text(
                        text = "Reported on: $formattedDate"
                    )
                    Text(text = "Reporter: ${report.fullName}")
                    Text(
                        text = "Phone: ${report.phoneNumber}",
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable {
                            // Create intent to dial the number
                            val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                                data = "tel:${report.phoneNumber}".toUri()
                            }
                            context.startActivity(dialIntent)
                        }
                    )
                    Text(text = report.details)
                    Row(
                        modifier = modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Min),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ElevatedButton(
                            modifier = modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.elevatedButtonColors(
                                containerColor = Color.Red,
                                contentColor = Color.White
                            ),
                            onClick = {
                                selectedReport = null
                            }
                        ) {
                            Text("Close")
                        }
                        ElevatedButton(
                            modifier = modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.elevatedButtonColors(
                                containerColor = Blue,
                                contentColor = Color.White
                            ),
                            onClick = {
                                val gmmIntentUri =
                                    "google.navigation:q=${report.latitude},${report.longitude}".toUri()
                                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                                mapIntent.setPackage("com.google.android.apps.maps")
                                context.startActivity(mapIntent)
                            }
                        ) {
                            Text("Navigate")
                        }
                    }
                }
            }
        }
    }
}
