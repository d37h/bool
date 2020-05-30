package ru.rsreu.astrukov.bool.model.element

import ru.rsreu.astrukov.bool.model.DrawParams

data class BoolElementFunction(
        val type: BoolElementType,

        val firstChild: BoolElementVariable?,
        val secondChild: BoolElementVariable?,

        override var drawParams: DrawParams? = null
) : BoolElement
