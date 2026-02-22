package com.example.tabangapp.db

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tabangapp.RetrofitInstance
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _isUserInserted = mutableStateOf(false)
    val isUserInserted: State<Boolean> = _isUserInserted

    private val _isReportInserted = mutableStateOf(false)
    val isReportInserted: State<Boolean> = _isReportInserted

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    private val _registerSuccessMessage = mutableStateOf<String?>(null)
    val registerSuccessMessage: State<String?> = _registerSuccessMessage

    private val _insertReportSuccessMessage = mutableStateOf<String?>(null)
    val insertReportSuccessMessage: State<String?> = _insertReportSuccessMessage

    private val _logoutMessage = mutableStateOf<String?>(null)
    val logoutMessage: State<String?> = _logoutMessage

    private val _refreshMessage = mutableStateOf<String?>(null)
    val refreshMessage: State<String?> = _refreshMessage

    private val getAllUser: LiveData<List<User>>
    private val repository: MainRepository

    private val _loginSuccess = mutableStateOf(false)
    val loginSuccess: State<Boolean> = _loginSuccess

    private val _logoutSuccess = mutableStateOf(false)
    val logoutSuccess: State<Boolean> = _logoutSuccess

    private val _longitude = mutableStateOf("")
    val longitude: State<String> = _longitude

    private val _latitude = mutableStateOf("")
    val latitude: State<String> = _latitude

    private val _userLocation = mutableStateOf<LatLng?>(null)
    val userLocation: MutableState<LatLng?> = _userLocation

    private val _reports = MutableStateFlow<List<Report>>(emptyList())
    val reports: StateFlow<List<Report>> = _reports

    private val _reportsPerUser = MutableStateFlow<List<Report>>(emptyList())
    val reportsPerUser: StateFlow<List<Report>> = _reportsPerUser

    private val _currentUser = mutableStateOf<User?>(null)
    val currentUser: MutableState<User?> = _currentUser

//    private val _currentUser = mutableStateOf<User?>(null)
//    val currentUser: MutableState<User?> = _currentUser

//    var currentUser: User? = null
    private val startOfDay: Long
    private val endOfDay: Long

    init {
        val mainDao = MainDatabase.getDatabase(application).mainDao()
        repository = MainRepository(mainDao)
        getAllUser = repository.readAllData

        val calendar = java.util.Calendar.getInstance()

        // Start of today
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        startOfDay = calendar.timeInMillis

        // End of today
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 23)
        calendar.set(java.util.Calendar.MINUTE, 59)
        calendar.set(java.util.Calendar.SECOND, 59)
        calendar.set(java.util.Calendar.MILLISECOND, 999)
        endOfDay = calendar.timeInMillis
    }

     fun updateLoggedInAs(loggedInAs: String){
        viewModelScope.launch {
            _currentUser.value?.let { obj ->
                repository.updateUserLoggedInAs(
                    obj.username,
                    loggedInAs
                )
                _currentUser.value = repository.getUserByUsername(obj.username)
            }
        }
    }

    fun checkLastUserStatus() {
        viewModelScope.launch {
            val user = repository.getUserLastUser()
            _currentUser.value = user
            if(user != null){
                if(user.isLoggedIn){
                    _currentUser.value?.let { obj ->
                            repository.updateUserLoginStatus(
                                obj.username,
                                obj.loggedInAs,
                                true
                            )
                        _currentUser.value = repository.getUserByUsername(obj.username)
                        login(obj.username, obj.password, obj.loggedInAs)
                    }
                }
            }
        }
    }

    fun fetchAllReports() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val result = RetrofitInstance.api.getAllReports()
                repository.insertReports(result)
                _reports.value = result
                _isLoading.value = false
                _refreshMessage.value = "Successfully loaded new reports 🎉"
            } catch (e: Exception) {
                e.printStackTrace()
                _isLoading.value = false
                _refreshMessage.value = null
            }
        }
    }

    private fun uriToMultipart(context: Context, uri: Uri): MultipartBody.Part {
        val contentResolver = context.contentResolver
        val inputStream = contentResolver.openInputStream(uri)!!
        val file = File(context.cacheDir, "upload_image.jpg")

        file.outputStream().use { output ->
            inputStream.copyTo(output)
        }

        val requestFile = file
            .asRequestBody("image/*".toMediaTypeOrNull())

        return MultipartBody.Part.createFormData(
            "image",
            file.name,
            requestFile
        )
    }


    fun apiAddReport(context: Context, report: Report, imageUri: Uri?){
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val userId = report.userId.toString()
                    .toRequestBody("text/plain".toMediaTypeOrNull())

                val fullName = report.fullName
                    .toRequestBody("text/plain".toMediaTypeOrNull())

                val phoneNumber = report.phoneNumber
                    .toRequestBody("text/plain".toMediaTypeOrNull())

                val details = report.details
                    .toRequestBody("text/plain".toMediaTypeOrNull())

                val longitude = report.longitude
                    .toRequestBody("text/plain".toMediaTypeOrNull())

                val latitude = report.latitude
                    .toRequestBody("text/plain".toMediaTypeOrNull())

                val imagePart = imageUri?.let {
                    uriToMultipart(context, it)
                }
                val result = RetrofitInstance.api.addReport(
                    userId,
                    fullName,
                    phoneNumber,
                    details,
                    longitude,
                    latitude,
                    imagePart
                )
                repository.insertReport(result)
                _isReportInserted.value = true
                _insertReportSuccessMessage.value = "Successfully added 🎉"
            } catch (e: retrofit2.HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val message = try {
                    val json = org.json.JSONObject(errorBody ?: "{}")
                    json.optString("detail", "Unknown error ❌")
                } catch (ex: Exception) {
                    "Unknown error ❌"
                }
                _errorMessage.value = "$message ❌"
            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.value = "${e.message} ❌"
            }
        }
    }

    fun apiRegister(user: User) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val result = RetrofitInstance.api.register(user)
                repository.addUser(result)
                _isUserInserted.value = true
                _registerSuccessMessage.value = "Registration successful 🎉"
            } catch (e: retrofit2.HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val message = try {
                    val json = org.json.JSONObject(errorBody ?: "{}")
                    json.optString("detail", "Unknown error ❌")
                } catch (ex: Exception) {
                    "Unknown error ❌"
                }
                _errorMessage.value = "$message ❌"
            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.value = "${e.message} ❌"
            }
        }
    }

    fun updateUserLocation(location: LatLng){
        _userLocation.value = location
        _longitude.value = location.longitude.toString()
        _latitude.value = location.latitude.toString()
    }

    fun updateLongitude(longitude: String){
        _longitude.value = longitude
    }

    fun updateLatitude(latitude: String){
        _latitude.value = latitude
    }

    fun getAllReports(onResult: (List<Report>) -> Unit) {
        viewModelScope.launch {
            val reports = repository.getAllReports()
            onResult(reports)
        }
    }


    fun login(username: String, password: String, loggedInAs: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val user = repository.getUserByUsername(username)
            if(user?.password == password){
                _loginSuccess.value = true
                repository.updateUserLoginStatus(user.username, loggedInAs, true)
                _currentUser.value = repository.getUserByUsername(user.username)
            } else {
                _errorMessage.value = "Invalid username or password ❌"
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _isLoading.value = true
            _currentUser.value?.let { user ->
                repository.updateUserLoginStatus(
                    user.username,
                    user.loggedInAs,
                    false
                )
                _currentUser.value = repository.getUserByUsername(user.username)
            }
            _logoutSuccess.value = true
            _logoutMessage.value = "You have been logged out 🎉"
        }
    }

    fun insertReport(
        userId: Int,
        details: String,
        fullName: String,
        phoneNumber: String,
        longitude: String,
        latitude: String,
        imageUri: String?
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            val report = Report(
                userId = userId,
                details = details,
                fullName = fullName,
                phoneNumber = phoneNumber,
                longitude = longitude,
                latitude = latitude,
                imageUri = imageUri
            )
            repository.insertReport(report)
            _isReportInserted.value = true
        }
    }

    fun getReportsForCurrentUser(onResult: (List<Report>) -> Unit) {
        _currentUser.value?.let { user ->
            viewModelScope.launch {
                val reports = repository.getReportsForUser(user.id)
                onResult(reports)
            }
        }
    }

    suspend fun validateUser(username: String, password: String): Boolean {
        val user = repository.getUserByUsername(username)
        return user?.password == password
    }

    fun addUser(user: User){
        viewModelScope.launch {
            _isLoading.value = true
            val existingUser = repository.getUserByUsername(user.username)
            if (existingUser != null) {
                _errorMessage.value = "Username already exists ❌"
                return@launch
            }
            repository.addUser(user)
            _isUserInserted.value = true
        }
    }

    fun resetLogoutState() {
        _isLoading.value = false
        _logoutSuccess.value = false
        _logoutMessage.value = null
    }

    fun resetInsertReportState() {
        _isLoading.value = false
        _isReportInserted.value = false
        _insertReportSuccessMessage.value = null
    }

    fun resetLoginState() {
        _isLoading.value = false
        _loginSuccess.value = false
        _errorMessage.value = null
    }

    fun resetInsertState() {
        _isLoading.value = false
        _isUserInserted.value = false
        _registerSuccessMessage.value = null
    }

    fun resetError() {
        _isLoading.value = false
        _errorMessage.value = null
    }
}