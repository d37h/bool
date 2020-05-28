package ru.rsreu.astrukov.bool.model

data class BoolElementVariable(
        val variable: String,
        override val drawing: BoolElementDrawing? = null
) : BoolElement