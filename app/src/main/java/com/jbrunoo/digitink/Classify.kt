package com.jbrunoo.digitink

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import com.jbrunoo.digitink.ml.MnistModel
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer


fun classify(context: Context, imageBitmap: ImageBitmap, imageProcessor: ImageProcessor): FloatArray {
    val model = MnistModel.newInstance(context)
    var tensorImage = TensorImage(DataType.UINT8)
    val bitmap = imageBitmap.asAndroidBitmap().copy(Bitmap.Config.ARGB_8888, true) // java.lang.IllegalArgumentException: Only supports loading ARGB_8888 bitmaps.
    tensorImage.load(bitmap) // tensorImage에 compose bitmap 없음
    tensorImage = imageProcessor.process(tensorImage)

    // Creates inputs for reference.
    val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 28, 28, 1), DataType.FLOAT32)

    Log.d("tensorImage buffer shape", tensorImage.toString())
    Log.d("shape", inputFeature0.buffer.toString())
    inputFeature0.loadBuffer(tensorImage.buffer)

    // Runs model inference and gets result.
    val outputs = model.process(inputFeature0)
    val outputFeature0 = outputs.outputFeature0AsTensorBuffer
    val confidence = outputFeature0.floatArray

    var maxPos = 0
    var maxConfidence = 0f

    for (i in confidence.indices) {
        if(confidence[i] > maxConfidence) {
            maxConfidence = confidence[i]
            maxPos = i
        }
    }

    model.close()

    return confidence
}