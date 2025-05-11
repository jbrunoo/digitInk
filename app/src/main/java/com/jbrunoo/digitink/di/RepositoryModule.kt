package com.jbrunoo.digitink.di

import com.jbrunoo.digitink.data.repository.ClassifierRepositoryImpl
import com.jbrunoo.digitink.data.repository.ResultRepositoryImpl
import com.jbrunoo.digitink.data.repository.TicketRepositoryImpl
import com.jbrunoo.digitink.domain.repository.ClassifierRepository
import com.jbrunoo.digitink.domain.repository.ResultRepository
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
    abstract fun bindResultRepository(repositoryImpl: ResultRepositoryImpl): ResultRepository

    @Binds
    @Singleton
    abstract fun bindTicketRepository(ticketRepositoryImpl: TicketRepositoryImpl): TicketRepository
}
