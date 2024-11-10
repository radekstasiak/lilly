package io.radev.lilly.ui.upload

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import android.content.ContentResolver
import dagger.hilt.android.lifecycle.HiltViewModel
import io.radev.lilly.domain.AnalyzeImageBase64WithGptUseCase
import io.radev.lilly.domain.AnalyzeImageUrlWithGptUseCase
import io.radev.lilly.domain.UploadPhotoToFirebaseUseCase
import java.io.FileOutputStream
import java.io.InputStream
import javax.inject.Inject

@HiltViewModel
class UploadViewModel @Inject constructor(
    private val analyzeImageUrlWithGptUseCase: AnalyzeImageUrlWithGptUseCase,
    private val analyzeImageBase64WithGptUseCase: AnalyzeImageBase64WithGptUseCase,
    private val uploadPhotoToFirebaseUseCase: UploadPhotoToFirebaseUseCase
) : ViewModel() {

    private val _uploadState = MutableStateFlow<UploadState>(UploadState.Idle)
    val uploadState: StateFlow<UploadState> get() = _uploadState

    fun analyzeImageUrlWithGPT(imageUrl: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _uploadState.value = analyzeImageUrlWithGptUseCase(imageUrl)
        }
    }

    fun analyzeImageBase64WithGPT(imageUrl: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            _uploadState.value = analyzeImageBase64WithGptUseCase(imageUrl)
        }
    }

    fun uploadPhotoToFirebase(
        uri: Uri
    ) {
        viewModelScope.launch {
            when (val response =
                uploadPhotoToFirebaseUseCase(
                    imageUri = uri
                )
            ) {
                is UploadState.Success ->
                    analyzeImageUrlWithGPT(
                        imageUrl = response.result
                    )

                is UploadState.Error ->
                    _uploadState.value = response

                UploadState.Idle -> {}
            }
        }
    }
    
}

sealed class UploadState {
    object Idle : UploadState()
    data class Success(val result: String) : UploadState()
    data class Error(val message: String) : UploadState()
}
