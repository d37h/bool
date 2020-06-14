package ru.rsreu.astrukov.bool.model.element

import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.scene.shape.StrokeType
import ru.rsreu.astrukov.bool.model.Coordinates

interface BoolElement {
    var coordinates: Coordinates?
    var parent: BoolElement?
}

interface BoolElementWithChildren : BoolElement {
    val firstChild: BoolElement?
    val secondChild: BoolElement?
    override var coordinates: Coordinates?
    override var parent: BoolElement?
}


