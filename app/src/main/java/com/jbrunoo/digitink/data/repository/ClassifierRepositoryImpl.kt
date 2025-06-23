package com.jbrunoo.digitink.data.repository

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import com.jbrunoo.digitink.data.dataSource.local.ClassifierLocalDataSource
import com.jbrunoo.digitink.domain.repository.ClassifierRepository
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import javax.inject.Inject

class ClassifierRepositoryImpl
@Inject
constructor(
    private val classifierLocalDataSource: ClassifierLocalDataSource,
) : ClassifierRepository {
    private val model = classifierLocalDataSource.model
    private val imageProcessor = classifierLocalDataSource.imageProcessor

    override fun classify(imageBitmap: ImageBitmap): Int {
        var tensorImage = TensorImage(DataType.UINT8)
        tensorImage.load(imageBitmap.asAndroidBitmap().copy(Bitmap.Config.ARGB_8888, false))
        tensorImage = imageProcessor.process(tensorImage)

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
}
