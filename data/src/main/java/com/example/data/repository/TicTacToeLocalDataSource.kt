package com.example.data.repository

import com.example.domain.model.TicTacToe


interface TicTacToeLocalDataSource {
    suspend fun getTicTacToe(): MutableList<TicTacToe?>
}