package com.example.tictactoe.ui

import com.example.common.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor() : BaseViewModel() {
    internal val mapOfBoard: HashMap<Int, PlayerState?> = hashMapOf()
    internal var playerState = PlayerState.X

    init {
        resetBoardState()
    }

    internal fun resetBoardState() {
        playerState = PlayerState.X
        mapOfBoard[1] = null
        mapOfBoard[2] = null
        mapOfBoard[3] = null
        mapOfBoard[4] = null
        mapOfBoard[5] = null
        mapOfBoard[6] = null
        mapOfBoard[7] = null
        mapOfBoard[8] = null
        mapOfBoard[9] = null
    }

}



