package ru.rsreu.astrukov.bool.model.element

import ru.rsreu.astrukov.bool.model.Coordinates
import ru.rsreu.astrukov.bool.model.DrawParams

data class BoolElementFunction(
        val type: BoolElementType,

        override val firstChild: BoolElementVariable?,
        override val secondChild: BoolElementVariable?,
        override var coordinates: Coordinates? = null
) : BoolElementWithChildren
