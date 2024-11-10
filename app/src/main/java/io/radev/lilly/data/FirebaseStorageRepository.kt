package io.radev.lilly.data

import android.net.Uri
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import io.radev.lilly.ui.upload.UploadState
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class FirebaseStorageRepository @Inject constructor() {
    private val storage by lazy { Firebase.storage }

    suspend fun uploadImageAndGetUrl(
        imageUri: Uri
    ): UploadState =
        try {
            val storageRef = storage.reference
            val imagesRef = storageRef.child("images/${UUID.randomUUID()}.jpg")
            imagesRef.putFile(imageUri).await()
            val downloadUrl = imagesRef.downloadUrl.await()
            UploadState.Success(downloadUrl.toString())
        } catch (e: Exception) {
            UploadState.Error(e.toString())
        }
//            .addOnSuccessListener {
//                imagesRef.downloadUrl.addOnSuccessListener { downloadUri ->
//                    Log.d("upload result", downloadUri.toString())
//                    onSuccess(downloadUri.toString())
//                }
//            }
//            .addOnFailureListener {
//                Log.d("upload result", it.toString())
//                onFailure(it.toString())
//            }

}
