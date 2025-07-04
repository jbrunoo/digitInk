package com.jbrunoo.digitink.di

import android.content.Context
import com.jbrunoo.digitink.ml.MnistModel
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
    fun provideMnistModel(@ApplicationContext context: Context): MnistModel = MnistModel.newInstance(context)
}
