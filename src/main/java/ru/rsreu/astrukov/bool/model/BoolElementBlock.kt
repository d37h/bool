package ru.rsreu.astrukov.bool.model

data class BoolElementBlock(
        val type: BoolElementType,

        val firstChild: BoolElement?,
        val secondChild: BoolElement?,

        val excludedVariable: String? = null,
        val variables: List<String>,
        val function: BoolFunction,

        override val drawing: BoolElementDrawing? = null,
        override var width: Double? = null,
        override var depth: Double? = null
) : BoolElement

data class BoolElementFunction(
        val type: String,

        val firstChild: BoolElement?,
        val secondChild: BoolElement?,


        override val drawing: BoolElementDrawing? = null,
        override var width: Double? = null,
        override var depth: Double? = null
) : BoolElement

