package com.jbrunoo.digitink.domain

import androidx.compose.ui.graphics.ImageBitmap

interface Classifier {
    fun classify(imageBitmap: ImageBitmap): Int
}