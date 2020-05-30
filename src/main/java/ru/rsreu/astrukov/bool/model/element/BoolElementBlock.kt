package ru.rsreu.astrukov.bool.model.element

import ru.rsreu.astrukov.bool.model.BoolFunction
import ru.rsreu.astrukov.bool.model.DrawParams

data class BoolElementBlock(
        val firstChild: BoolElement?,
        val secondChild: BoolElement?,

        val excludedVariable: String? = null,
        val variables: List<String>,
        val function: BoolFunction,

        override var drawParams: DrawParams? = null
) : BoolElement
