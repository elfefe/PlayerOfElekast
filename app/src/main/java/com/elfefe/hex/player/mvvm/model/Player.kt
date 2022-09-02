package com.elfefe.hex.player.mvvm.model

data class Player(val id: String, val name: String, val email: String, val visible: Boolean, var folder: String? = null)
