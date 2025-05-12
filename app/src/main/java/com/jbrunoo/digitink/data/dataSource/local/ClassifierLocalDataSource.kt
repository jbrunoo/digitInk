package com.jbrunoo.digitink.data.dataSource.local

import com.jbrunoo.digitink.ml.MnistModel
import org.tensorflow.lite.support.image.ImageProcessor

interface ClassifierLocalDataSource {

    val model: MnistModel

    val imageProcessor: ImageProcessor
}
