package com.jbrunoo.digitink.di

import com.jbrunoo.digitink.playgames.PlayGamesManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class PlayGamesModule {

    @Provides
    @Singleton
    fun providePlayGamesManager() = PlayGamesManager()
}