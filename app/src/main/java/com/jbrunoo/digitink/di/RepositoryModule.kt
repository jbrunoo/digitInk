package com.jbrunoo.digitink.di

import android.content.Context
import com.jbrunoo.digitink.data.ResultRepositoryImpl
import com.jbrunoo.digitink.domain.ResultRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideResultRepository(@ApplicationContext context: Context): ResultRepository =
        ResultRepositoryImpl(context)
}