package com.example.seguritas.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.io.InputStream

class ImageViewModel : ViewModel() {
    var selectedImage: ImageBitmap? by mutableStateOf(null)
        private set

    fun setImage(bitmap: Bitmap?) {
        selectedImage = bitmap?.asImageBitmap()
    }

    fun clearImage() {
        selectedImage = null
    }

    fun loadImageFromStream(inputStream: InputStream?) {
        viewModelScope.launch {
            val bitmap = BitmapFactory.decodeStream(inputStream)
            setImage(bitmap)
        }
    }
}
