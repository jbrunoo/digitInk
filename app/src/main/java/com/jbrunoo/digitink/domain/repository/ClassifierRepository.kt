package com.jbrunoo.digitink.domain.repository

import androidx.compose.ui.graphics.ImageBitmap

interface ClassifierRepository {
    fun classify(imageBitmap: ImageBitmap): Int
}
