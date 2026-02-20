package com.example.tabangapp.screens.register


import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tabangapp.db.MainViewModel
import com.example.tabangapp.db.User
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import coil.compose.rememberAsyncImagePainter
import com.example.tabangapp.R
import com.example.tabangapp.ui.theme.PurpleGrey40


@Composable
fun Register(
    navController: NavController,
    mainViewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("12345") }
    var confirmPassword by remember { mutableStateOf("12345") }
    var fullName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("12345") }
    var passwordVisible by remember { mutableStateOf(false) }
    val isLoading = mainViewModel.isLoading.value
    val snackbarHostState = remember { SnackbarHostState() }
    val isUserInserted = mainViewModel.isUserInserted.value
    val errorMessage = mainViewModel.errorMessage.value


    Scaffold(
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
                    text = "REGISTER",
                    fontSize = 30.sp
                )
                OutlinedTextField(
                    value = fullName,
                    enabled = !isLoading,
                    onValueChange = {
                        fullName = it
                        username = it
                            .trim()
                            .lowercase()
                            .replace(Regex("\\s+"), "")
                    },
                    modifier = modifier.fillMaxWidth(),
                    label = { Text("Full name *") },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    isError = fullName.isBlank()
                )
                OutlinedTextField(
                    value = phoneNumber,
                    enabled = !isLoading,
                    onValueChange = { phoneNumber = it },
                    modifier = modifier.fillMaxWidth(),
                    label = { Text("Phone number *") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Phone
                    ),
                    shape = RoundedCornerShape(8.dp),
                    isError = phoneNumber.isBlank()
                )
                OutlinedTextField(
                    value = username,
                    enabled = false,
                    onValueChange = {},
                    modifier = modifier.fillMaxWidth(),
                    label = { Text("Username") },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                )
                OutlinedTextField(
                    value = password,
                    enabled = !isLoading,
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

                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = image,
                                contentDescription = "Toggle password visibility"
                            )
                        }
                    }
                )
                OutlinedTextField(
                    value = confirmPassword,
                    enabled = !isLoading,
                    onValueChange = { confirmPassword = it },
                    modifier = modifier.fillMaxWidth(),
                    label = { Text("Confirm your password") },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    visualTransformation = if (passwordVisible)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),
                )
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
                            fullName.isNotBlank() &&
                            phoneNumber.isNotBlank() &&
                            password.isNotBlank() &&
                            confirmPassword.isNotBlank()
                        ) {
                            if (password == confirmPassword) {
                                val user = User(
                                    username = username,
                                    password = password,
                                    fullName = fullName,
                                    phoneNumber = phoneNumber
                                )
                                mainViewModel.addUser(user)
                            }
                        }
                    }
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 1.dp,
                        )
                    } else {
                        Text("Register")
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Already have an account?",
                        fontSize = 12.sp
                    )
                    Text(
                        text = "Login",
                        fontSize = 12.sp,
                        textDecoration = TextDecoration.Underline,
                        color = Color.Black,
                        modifier = modifier.clickable {
                            navController.navigate("start")
                        }
                    )
                }
            }
        }
    }
    LaunchedEffect(isUserInserted) {
        if (isUserInserted) {
            snackbarHostState.showSnackbar("Registration successful 🎉")
            mainViewModel.resetInsertState()
            navController.navigate("start")
        }
    }
    LaunchedEffect(errorMessage) {
        if (errorMessage != null) {
            snackbarHostState.showSnackbar(errorMessage)
            mainViewModel.resetError()
        }
    }
}
