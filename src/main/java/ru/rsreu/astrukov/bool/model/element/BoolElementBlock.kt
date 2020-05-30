package ru.rsreu.astrukov.bool.model.element

import ru.rsreu.astrukov.bool.model.BoolFunction
import ru.rsreu.astrukov.bool.model.DrawParams

data class BoolElementBlock(
        val excludedVariable: String? = null,
        val variables: List<String>,
        val function: BoolFunction,

        override var drawParams: DrawParams? = null,
        override val firstChild: BoolElement?,
        override val secondChild: BoolElement?
) : BoolElementWithChildren
