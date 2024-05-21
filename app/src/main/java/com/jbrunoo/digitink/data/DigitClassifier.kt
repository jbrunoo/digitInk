package com.jbrunoo.digitink.data

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import com.jbrunoo.digitink.domain.Classifier
import com.jbrunoo.digitink.ml.MnistModel
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.image.ops.TransformToGrayscaleOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.inject.Inject

class DigitClassifier @Inject constructor(private val model: MnistModel) : Classifier {
    override fun classify(imageBitmap: ImageBitmap): Int {


        var tensorImage = TensorImage(DataType.UINT8)
        tensorImage.load(imageBitmap.asAndroidBitmap().copy(Bitmap.Config.ARGB_8888, false))

        val imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(28, 28, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
            .add(NormalizeOp(0.0f, 255.0f))  // 이 줄 추가 안해서 입력값 달랐음 (1, 150, 150, 3)
            .add(TransformToGrayscaleOp()) // 회색조 이미지, 라이브러리 tensorflow lite support 필요
            .build()
        tensorImage = imageProcessor.process(tensorImage)

        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 28, 28, 1), DataType.FLOAT32)

//        val androidBmp = imageBitmap.asAndroidBitmap()
//        val resizedBmp = resizeBitmap(androidBmp)
//        val grayByteBuffer = convertBitmapGrayByteBuffer(resizedBmp)
        inputFeature0.loadBuffer(tensorImage.buffer)

        Log.d("compare buffer shape", "${tensorImage.buffer} / ${inputFeature0.buffer}")

        // Runs model inference and gets result.
        val outputs = model.process(inputFeature0)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer

        var maxPos = 0
        var maxConfidence = 0f

        outputFeature0.floatArray.forEachIndexed { index, confidence ->
            Log.d("confidence", "$confidence")
            if (confidence > maxConfidence) {
                maxConfidence = confidence
                maxPos = index
            }
        }

        model.close()

        return maxPos
    }

    private fun resizeBitmap(bitmap: Bitmap) =
        Bitmap.createScaledBitmap(bitmap, 28, 28, false)

    private fun convertBitmapGrayByteBuffer(bitmap: Bitmap): ByteBuffer {
        val byteBuffer = ByteBuffer.allocateDirect(bitmap.byteCount)
        byteBuffer.order(ByteOrder.nativeOrder())

        val pixels = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        pixels.forEach { pixel ->
            val r = pixel shr 16 and 0xFF
            val g = pixel shr 8 and 0xFF
            val b = pixel and 0xFF

            val avgPixelValue = (r + g + b) / 3.0f
            val normalizedPixelValue = avgPixelValue / 255.0f

            byteBuffer.putFloat(normalizedPixelValue)
        }
        return byteBuffer
    }

}

