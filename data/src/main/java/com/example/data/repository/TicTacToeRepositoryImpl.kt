package com.example.data.repository

import com.example.domain.model.ResultData
import com.example.domain.model.TicTacToe
import com.example.domain.repository.TicTacToeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class TicTacToeRepositoryImpl @Inject constructor(
    private val localDataSource: TicTacToeLocalDataSource
) : TicTacToeRepository {

    override fun getTicTacToe(): Flow<ResultData<MutableList<TicTacToe?>>> = flow {
        emit(ResultData.Loading())
        try {
            emit(ResultData.Success(localDataSource.getTicTacToe()))
        } catch (e: Exception) {
            emit(ResultData.Failed(errorMessage = e.message))
        }
    }


}