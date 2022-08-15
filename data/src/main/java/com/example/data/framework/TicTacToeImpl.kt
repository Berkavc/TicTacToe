package com.example.data.framework

import com.example.common.Work
import com.example.domain.model.TicTacToe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

class TicTacToeImpl @Inject
constructor() {
    private var coroutineScope = CoroutineScope(Dispatchers.IO)
    fun getTictactoe(): Work<MutableList<TicTacToe?>> {
        val work = Work<MutableList<TicTacToe?>>()
        try {
            coroutineScope.launch {
                async {
                    val tictacToeList = mutableListOf<TicTacToe?>()
                    work.onSuccess(tictacToeList)
                }
            }
        } catch (e: Exception) {
            work.onFailure(e)
        }
        return work
    }


}