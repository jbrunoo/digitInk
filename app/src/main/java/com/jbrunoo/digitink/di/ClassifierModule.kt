package com.jbrunoo.digitink.di

import android.content.Context
import com.jbrunoo.digitink.data.DigitClassifier
import com.jbrunoo.digitink.domain.Classifier
import com.jbrunoo.digitink.ml.MnistModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ModelModule {
    @Provides
    @Singleton
    fun provideModel(@ApplicationContext context: Context): MnistModel {
        return MnistModel.newInstance(context)
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class ClassifierModule {

    @Binds
    @Singleton
    abstract fun bindClassifier(impl: DigitClassifier): Classifier

    companion object {
        @Provides
        @Singleton
        fun provideDigitClassifier(model: MnistModel): DigitClassifier {
            return DigitClassifier(model)
        }
    }
}