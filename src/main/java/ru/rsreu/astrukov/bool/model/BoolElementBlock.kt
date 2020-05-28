package ru.rsreu.astrukov.bool.model

data class BoolElementBlock(
        val type: BoolElementType,

        val firstChild: BoolElement?,
        val secondChild: BoolElement?,

        val excludedVariable: String? = null,
        val variables: List<String>,
        val function: String,

        override val drawing: BoolElementDrawing? = null
) : BoolElement

