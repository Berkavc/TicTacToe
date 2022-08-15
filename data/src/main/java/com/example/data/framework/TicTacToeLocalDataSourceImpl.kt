package com.example.data.framework

import com.example.data.repository.TicTacToeLocalDataSource
import com.example.domain.model.TicTacToe
import javax.inject.Inject
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class TicTacToeLocalDataSourceImpl @Inject constructor(private val ticTacToeImpl: TicTacToeImpl) :
    TicTacToeLocalDataSource {
    override suspend fun getTicTacToe(): MutableList<TicTacToe?> {
        return suspendCoroutine { continuation ->
            ticTacToeImpl.getTictactoe()
                .addOnSuccessListener {
                    continuation.resumeWith(Result.success(it))
                }.addOnFailureListener {
                    continuation.resumeWithException(it)
                }
        }
    }
}