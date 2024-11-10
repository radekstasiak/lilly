package io.radev.lilly.domain

import android.net.Uri
import io.radev.lilly.ui.upload.UploadState
import io.radev.lilly.data.FirebaseStorageRepository
import javax.inject.Inject

class UploadPhotoToFirebaseUseCase @Inject constructor(
    private val firebaseStorageRepository: FirebaseStorageRepository
) {

    suspend operator fun invoke(
        imageUri: Uri
    ): UploadState =
        firebaseStorageRepository.uploadImageAndGetUrl(
            imageUri = imageUri
        )
}
