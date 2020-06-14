package ru.rsreu.astrukov.bool.model.element

import ru.rsreu.astrukov.bool.model.Coordinates
import ru.rsreu.astrukov.bool.model.DrawParams

data class BoolElementFunction(
        val type: BoolElementType,

        override var firstChild: BoolElementVariable?,
        override var secondChild: BoolElementVariable?,
        override var coordinates: Coordinates? = null,
        override var parent: BoolElement? = null
) : BoolElementWithChildren
