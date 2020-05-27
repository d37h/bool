package ru.rsreu.astrukov.bool.model

data class BoolElementVariable(
        val variable: String,

        override val posX: Int = 0,
        override val posY: Int = 0
) : BoolElement