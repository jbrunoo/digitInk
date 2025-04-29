package com.jbrunoo.digitink.di

import com.jbrunoo.digitink.data.ResultRepositoryImpl
import com.jbrunoo.digitink.data.TicketRepositoryImpl
import com.jbrunoo.digitink.domain.ResultRepository
import com.jbrunoo.digitink.domain.TicketRepository
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
    abstract fun bindResultRepository(repositoryImpl: ResultRepositoryImpl): ResultRepository

    @Binds
    @Singleton
    abstract fun bindTicketRepository(ticketRepositoryImpl: TicketRepositoryImpl): TicketRepository
}
