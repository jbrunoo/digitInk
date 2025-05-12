package com.jbrunoo.digitink.di

import com.jbrunoo.digitink.data.repository.ClassifierRepositoryImpl
import com.jbrunoo.digitink.data.repository.ScoreRepositoryImpl
import com.jbrunoo.digitink.data.repository.TicketRepositoryImpl
import com.jbrunoo.digitink.domain.repository.ClassifierRepository
import com.jbrunoo.digitink.domain.repository.ScoreRepository
import com.jbrunoo.digitink.domain.repository.TicketRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindClassifierRepository(repositoryImpl: ClassifierRepositoryImpl): ClassifierRepository

    @Binds
    @Singleton
    abstract fun bindResultRepository(repositoryImpl: ScoreRepositoryImpl): ScoreRepository

    @Binds
    @Singleton
    abstract fun bindTicketRepository(repositoryImpl: TicketRepositoryImpl): TicketRepository
}
