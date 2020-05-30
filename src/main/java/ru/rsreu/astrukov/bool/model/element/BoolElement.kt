package ru.rsreu.astrukov.bool.model.element

import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.scene.shape.StrokeType
import ru.rsreu.astrukov.bool.model.Coordinates
import ru.rsreu.astrukov.bool.model.DrawParams

interface BoolElement {
    var coordinates: Coordinates?
}

interface BoolElementWithChildren: BoolElement {
    val firstChild: BoolElement?
    val secondChild: BoolElement?
    override var coordinates: Coordinates?
}

fun styleRect(rectangle: Rectangle): Rectangle {

    return rectangle.apply {
        this.fill = Color.WHEAT
        this.stroke = Color.BLACK
        this.strokeWidth = strokeWidth
        this.strokeType = StrokeType.INSIDE
    }


}

