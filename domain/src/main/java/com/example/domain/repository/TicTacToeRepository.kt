package com.example.domain.repository

import com.example.domain.model.ResultData
import com.example.domain.model.TicTacToe
import kotlinx.coroutines.flow.Flow

interface TicTacToeRepository {

    fun getTicTacToe(): Flow<ResultData<MutableList<TicTacToe?>>>

}