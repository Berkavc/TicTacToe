package com.example.tictactoe.di.module

import com.example.data.framework.TicTacToeImpl
import com.example.data.framework.TicTacToeLocalDataSourceImpl
import com.example.data.repository.TicTacToeLocalDataSource
import com.example.data.repository.TicTacToeRepositoryImpl
import com.example.domain.repository.TicTacToeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object RepositoryModule {
    @Provides
    @Singleton
    fun providesTicTacToeRepository(
        localDataSource: TicTacToeLocalDataSource
    ): TicTacToeRepository {
        return TicTacToeRepositoryImpl(localDataSource)
    }

    @Provides
    @Singleton
    fun providesDeviceRoomImpl(): TicTacToeImpl {
        return TicTacToeImpl()
    }

    @Provides
    @Singleton
    fun providesTicTacToeLocalDataSource(): TicTacToeLocalDataSource {
        return TicTacToeLocalDataSourceImpl(providesDeviceRoomImpl())
    }
}