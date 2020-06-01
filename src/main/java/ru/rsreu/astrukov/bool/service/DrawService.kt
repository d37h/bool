package ru.rsreu.astrukov.bool.service

import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.scene.text.Font
import javafx.scene.text.Text
import ru.rsreu.astrukov.bool.model.Coordinates
import ru.rsreu.astrukov.bool.model.DrawParams
import ru.rsreu.astrukov.bool.model.DrawVariables
import ru.rsreu.astrukov.bool.model.element.*

class DrawService() {

    var drawParams = DrawParams(scale = 2.0)

    fun draw(element: BoolElement, node: Pane, offsetY: Double = 0.0) {

        if (element.coordinates == null) {
            return
        }

        when (element) {
            is BoolElementBlock -> {
                drawBIP(element.coordinates!!, node, offsetY)
            }
            is BoolElementFunction -> {
                drawFunction(element, element.coordinates!!, node, offsetY)
            }
            is BoolElementVariable -> {
                drawVariable(element, element.coordinates!!, node, offsetY)
            }
        }

        if (element is BoolElementWithChildren) {
            element.firstChild?.let {
                draw(it, node, offsetY)
            }
            element.secondChild?.let {
                draw(
                        it,
                        node,
                        offsetY + (element.firstChild?.coordinates?.elementHeight ?: 0.0)
                                + DrawVariables.spacingHeight
                )
            }
        }
    }


    fun setCoordinates(root: BoolElement, depth: Int) {
        if (root is BoolElementWithChildren) {
            if (root.firstChild != null) {
                setCoordinates(root.firstChild!!, depth + 1)
            }
            if (root.secondChild != null) {
                setCoordinates(root.secondChild!!, depth + 1)
            }

            root.coordinates = Coordinates(
                    depth = depth * newBlockOffsetX,
                    elementHeight = DrawVariables.spacingHeight +
                            (root.secondChild?.coordinates?.elementHeight ?: 0.0) +
                            (root.firstChild?.coordinates?.elementHeight ?: 0.0)
            )

            return
        }

        root.coordinates = Coordinates(
                depth = depth * newBlockOffsetX,
                elementHeight = DrawVariables.elementSubBlockHeight * 2
        )
    }

    private fun drawBIP(coordinates: Coordinates, node: Pane, offsetY: Double) {
        drawRect(coordinates, offsetY, Color.ORANGE, node)
    }

    private fun drawFunction(element: BoolElementFunction, coordinates: Coordinates, node: Pane, offsetY: Double) {
        drawRect(coordinates, offsetY, Color.ORANGE, node)

    }

    private fun drawVariable(element: BoolElementVariable, coordinates: Coordinates, node: Pane, offsetY: Double) {
        drawRect(coordinates, offsetY, Color.RED, node)
    }

    private fun drawRect(coordinates: Coordinates, offset: Double, color: Color, node: Pane) {
//        val offset = nornmalizeOffset(offsetY)

        val dbgRect = styleRect(Rectangle()).apply {
            x = coordinates.getPosX()
            y = offset * this@DrawService.drawParams.scale
            height = offset * this@DrawService.drawParams.scale + coordinates.elementHeight / 2
            width = this@DrawService.scaledSubBlockWidth
            fill = Color.TRANSPARENT
        }

        val dbgRect2 = styleRect(Rectangle()).apply {
            x = coordinates.getPosX()
            y = offset * this@DrawService.drawParams.scale + coordinates.elementHeight / 2
            height = offset * this@DrawService.drawParams.scale + coordinates.elementHeight / 2
            width = this@DrawService.scaledSubBlockWidth
            fill = Color.TRANSPARENT
        }

        val mainRect = styleRect(Rectangle()).apply {
            x = coordinates.getPosX()
            y = coordinates.getPosY(offset)
            height = this@DrawService.scaledSubBlockHeight * 2
            width = this@DrawService.scaledSubBlockWidth
            fill = color
        }

        node.children.addAll(listOf(mainRect, dbgRect, dbgRect2))
    }

    private fun styleText(text: Text): Text {
        text.rotate = 90.0
        text.fill = Color.BLACK
        text.font = Font("Verdana", this.drawParams.scale * DrawVariables.fontSize)

        return text
    }

    private fun Coordinates.getPosY(offsetY: Double): Double = (
            (this.elementHeight) / 2 + offsetY - DrawVariables.elementSubBlockHeight
            ) * this@DrawService.drawParams.scale


    private fun Coordinates.getPosX() = (this.depth) * this@DrawService.drawParams.scale

    val scaledSubBlockHeight = this.drawParams.scale * DrawVariables.elementSubBlockHeight
    val scaledSubBlockWidth = this.drawParams.scale * DrawVariables.elementSubBlockWidth

    val newBlockOffsetX = (DrawVariables.spacingWidth + DrawVariables.elementSubBlockWidth * 2)
}