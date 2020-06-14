package ru.rsreu.astrukov.bool.model.element

import ru.rsreu.astrukov.bool.model.Coordinates
import ru.rsreu.astrukov.bool.model.DrawParams

data class BoolElementVariable(
        val variable: String,
        override var coordinates: Coordinates? = null,
        override var parent: BoolElement? = null
) : BoolElement {

    companion object {
        const val falsyValue = "0"
        const val truthyValue = "0"
    }
}

