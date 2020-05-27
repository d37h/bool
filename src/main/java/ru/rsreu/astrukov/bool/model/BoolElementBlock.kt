package ru.rsreu.astrukov.bool.model

data class BoolElementBlock(
        val type: BoolElementType,

        val firstChild: BoolElement?,
        val secondChild: BoolElement?,

        val excludedVariable: String? = null,
        val variables: List<String>,
        val function: String,

        override val posX: Int = 0,
        override val posY: Int = 0
) : BoolElement

