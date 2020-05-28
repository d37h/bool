package ru.rsreu.astrukov.bool.model

import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.scene.shape.StrokeType

interface BoolElement {
    val drawing: BoolElementDrawing?
    var width: Double?
    var depth: Double?
}

fun draw(drawing: BoolElementDrawing, type: BoolElementType, node: Pane) {

    if (drawing.coordinates != null) {

        val blockHeight = drawing.scale * ELEMENT_SUBBLOCK_HEIGHT
        val blockWidth = drawing.scale * ELEMENT_SUBBLOCK_WIDTH

        if (type == BoolElementType.BIP) {
            val mainRect = Rectangle().let { drawing.styleRect(it) }

            mainRect.x = drawing.coordinates.posX
            mainRect.y = drawing.coordinates.posY
            mainRect.height = (2 * blockHeight - drawing.lineThickness)
            mainRect.width = (blockWidth)

            val firstChildRect = Rectangle().let { drawing.styleRect(it) }

            firstChildRect.x = drawing.coordinates.posX + blockWidth - drawing.lineThickness
            firstChildRect.y = drawing.coordinates.posY
            firstChildRect.height = blockHeight
            firstChildRect.width = blockWidth

            val secondChildRect = Rectangle().let { drawing.styleRect(it) }

            secondChildRect.x = drawing.coordinates.posX + blockWidth - drawing.lineThickness
            secondChildRect.y = drawing.coordinates.posY + blockHeight - drawing.lineThickness
            secondChildRect.height = blockHeight
            secondChildRect.width = blockWidth

            node.children.addAll(listOf(mainRect, firstChildRect, secondChildRect))

        }
    }
}

fun BoolElementDrawing.styleRect(rectangle: Rectangle): Rectangle {

    return rectangle.apply {
        this.fill = Color.WHEAT
        this.stroke = Color.BLACK
        this.strokeWidth = strokeWidth
        this.strokeType = StrokeType.INSIDE
    }


}

