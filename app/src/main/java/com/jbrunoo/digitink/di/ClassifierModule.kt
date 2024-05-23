package com.jbrunoo.digitink.di

import android.content.Context
import com.jbrunoo.digitink.data.DigitClassifier
import com.jbrunoo.digitink.domain.Classifier
import com.jbrunoo.digitink.ml.MnistModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.image.ops.TransformToGrayscaleOp
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ModelModule {
    @Provides
    @Singleton
    fun provideModel(@ApplicationContext context: Context): MnistModel {
        return MnistModel.newInstance(context)
    }

    @Provides
    @Singleton
    fun provideImgProcessor(): ImageProcessor {
        return ImageProcessor.Builder()
            .add(ResizeOp(28, 28, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
            .add(NormalizeOp(0.0f, 255.0f))  // 이 줄 추가 안해서 입력값 달랐음 (1, 150, 150, 3)
            .add(TransformToGrayscaleOp()) // 회색조 이미지, 라이브러리 tensorflow lite support 필요
            .build()
    }
}

@Module
@InstallIn(ViewModelComponent::class)
abstract class ClassifierModule {

    @Binds
    @ViewModelScoped
    abstract fun bindClassifier(impl: DigitClassifier): Classifier

    companion object {
        @Provides
        @ViewModelScoped
        fun provideDigitClassifier(model: MnistModel, imgProcessor: ImageProcessor): DigitClassifier {
            return DigitClassifier(model, imgProcessor)
        }
    }
}