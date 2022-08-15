package com.example.domain.model

import java.io.Serializable

data class TicTacToe(
    val id: String,
    val name: String? = null
) : Serializable
