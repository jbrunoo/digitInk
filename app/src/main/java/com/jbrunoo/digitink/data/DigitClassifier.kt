package com.jbrunoo.digitink.data

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import com.jbrunoo.digitink.domain.Classifier
import com.jbrunoo.digitink.ml.MnistModel
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import timber.log.Timber
import javax.inject.Inject

/* 모델은 singleton 생성, application terminate 시 model.close */
class DigitClassifier @Inject constructor(
    private val model: MnistModel,
    private val imgProcessor: ImageProcessor,
) : Classifier {

    override fun classify(imageBitmap: ImageBitmap): Int {

        var tensorImage = TensorImage(DataType.UINT8)
        tensorImage.load(imageBitmap.asAndroidBitmap().copy(Bitmap.Config.ARGB_8888, false))
        tensorImage = imgProcessor.process(tensorImage)

        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 28, 28, 1), DataType.FLOAT32)
        inputFeature0.loadBuffer(tensorImage.buffer)

//        Timber.tag(TAG).d("tImgBuf : $tensorImage.buffer | inputFeat0Buf : $inputFeature0.buffer")

        // Runs model inference and gets result.
        val outputs = model.process(inputFeature0)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer

        var maxPos = 0
        var maxConfidence = 0f

        outputFeature0.floatArray.forEachIndexed { index, confidence ->
//            Timber.tag(TAG).d("confidence : $confidence")
            if (confidence > maxConfidence) {
                maxConfidence = confidence
                maxPos = index
            }
        }

        return maxPos
    }

    companion object {
        private const val TAG = "DigitClassifier"
    }
}

