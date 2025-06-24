package com.jbrunoo.digitink.data.dataSource.local

import com.jbrunoo.digitink.ml.MnistModel
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.image.ops.TransformToGrayscaleOp
import javax.inject.Inject

class ClassifierLocalDataSourceImpl @Inject constructor(
    private val mnistModel: MnistModel,
) : ClassifierLocalDataSource {

    override val model: MnistModel by lazy { mnistModel }

    override val imageProcessor: ImageProcessor by lazy {
        ImageProcessor.Builder()
            .add(ResizeOp(28, 28, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
            .add(NormalizeOp(0.0f, 255.0f))
            .add(TransformToGrayscaleOp()) // 회색조 이미지, 라이브러리 tensorflow lite support 필요
            .build()
    }
}
