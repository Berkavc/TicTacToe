package com.example.domain.usecase

import com.example.domain.model.ResultData
import com.example.domain.model.TicTacToe
import com.example.domain.repository.TicTacToeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTicTacToeUseCase @Inject constructor(private val repository: TicTacToeRepository) {
    operator fun invoke(ticTacToeList: MutableList<TicTacToe?>): Flow<ResultData<MutableList<TicTacToe?>>> {
        return repository.getTicTacToe()
    }
}