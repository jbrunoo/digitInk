package com.jbrunoo.digitink.di

import com.jbrunoo.digitink.data.dataSource.local.ClassifierLocalDataSource
import com.jbrunoo.digitink.data.dataSource.local.ClassifierLocalDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LocalDataSourceModule {

    @Binds
    @Singleton
    abstract fun bindTokenDataSource(dataSourceImpl: ClassifierLocalDataSourceImpl): ClassifierLocalDataSource
}
