package ru.rsreu.astrukov.bool.service

import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Line
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

        if (element is BoolElementWithChildren) {
            element.firstChild?.let {
                val coordOffset = offsetY - DrawVariables.connectorOffset
                drawConnection(element.coordinates!!, element.firstChild!!.coordinates!!, coordOffset, offsetY, node)

                draw(it, node, offsetY)
            }
            element.secondChild?.let {
                val offset = offsetY + (element.firstChild?.coordinates?.elementHeight ?: 0.0
                        ) + DrawVariables.spacingHeight
                val coordOffset = offsetY + DrawVariables.connectorOffset
                drawConnection(element.coordinates!!, element.secondChild!!.coordinates!!, coordOffset, offset, node)

                draw(it, node, offset)

            }
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
        val r1 = styleRect(Rectangle()).apply {
            x = coordinates.getPosX()+this@DrawService.scaledSubBlockWidth
            y = coordinates.getPosY(offsetY)
            height = this@DrawService.scaledSubBlockHeight
            width = this@DrawService.scaledSubBlockWidth
        }

        val r2 = styleRect(Rectangle()).apply {
            x = coordinates.getPosX()+this@DrawService.scaledSubBlockWidth
            y = coordinates.getPosY(offsetY+DrawVariables.elementSubBlockWidth)
            height = this@DrawService.scaledSubBlockHeight
            width = this@DrawService.scaledSubBlockWidth
        }

        node.children.addAll(r1, r2)
    }

    private fun drawFunction(element: BoolElementFunction, coordinates: Coordinates, node: Pane, offsetY: Double) {
        drawRect(coordinates, offsetY, Color.ORANGE, node)

    }

    private fun drawVariable(element: BoolElementVariable, coordinates: Coordinates, node: Pane, offsetY: Double) {
        drawRect(coordinates, offsetY, Color.RED, node)
    }

    private fun drawRect(coordinates: Coordinates, offset: Double, color: Color, node: Pane) {

        val mainRect = styleRect(Rectangle()).apply {
            x = coordinates.getPosX()
            y = coordinates.getPosY(offset)
            height = this@DrawService.scaledSubBlockHeight * 2
            width = this@DrawService.scaledSubBlockWidth
            fill = color
        }

        node.children.addAll(mainRect)
    }

    private fun drawConnection(startCoords: Coordinates, endCoords: Coordinates, startOffsetY: Double, endOffsetY: Double, node: Pane) {
        val line = Line(
                startCoords.getTopConnectorPosXStart(),
                startCoords.getTopConnectorPosYStart(startOffsetY),
                endCoords.getTopConnectorPosXStart() - DrawVariables.elementSubBlockWidth*drawParams.scale,
                endCoords.getTopConnectorPosYStart(endOffsetY)
        )

        val line1 = Line(
                startCoords.getTopConnectorPosXStart(),
                startCoords.getTopConnectorPosYStart(startOffsetY),
                startCoords.getTopConnectorPosXStart() + drawParams.scale*(
                        DrawVariables.elementSubBlockWidth + DrawVariables.spacingWidth/2),
                startCoords.getTopConnectorPosYStart(startOffsetY)
        )

        val line2 = Line(
                startCoords.getTopConnectorPosXStart() + drawParams.scale*(
                        DrawVariables.elementSubBlockWidth + DrawVariables.spacingWidth/2),
                startCoords.getTopConnectorPosYStart(startOffsetY),
                startCoords.getTopConnectorPosXStart() + drawParams.scale*(
                        DrawVariables.elementSubBlockWidth + DrawVariables.spacingWidth/2),
                endCoords.getTopConnectorPosYStart(endOffsetY)
        )

        val line3 = Line(
                startCoords.getTopConnectorPosXStart() + drawParams.scale*(
                        DrawVariables.elementSubBlockWidth + DrawVariables.spacingWidth/2),
                endCoords.getTopConnectorPosYStart(endOffsetY),
                startCoords.getTopConnectorPosXStart() + drawParams.scale*(
                        DrawVariables.elementSubBlockWidth + DrawVariables.spacingWidth),
                endCoords.getTopConnectorPosYStart(endOffsetY)
        )

        line1.stroke = Color.BLACK
        line2.stroke = Color.BLACK
        line3.stroke = Color.BLACK
        node.children.addAll(line1, line2, line3)
    }

    private fun styleText(text: Text): Text {
        text.rotate = 90.0
        text.fill = Color.BLACK
        text.font = Font("Verdana", this.drawParams.scale * DrawVariables.fontSize)

        return text
    }

    private fun Coordinates.getPosY(offsetY: Double): Double = (
            this.elementHeight / 2 + offsetY - DrawVariables.elementSubBlockHeight
            ) * this@DrawService.drawParams.scale

    private fun Coordinates.getPosX() = (this.depth) * this@DrawService.drawParams.scale

    private fun Coordinates.getTopConnectorPosYStart(offsetY: Double) = (
            this.elementHeight / 2 + offsetY
            ) * this@DrawService.drawParams.scale

    private fun Coordinates.getTopConnectorPosXStart() = (
            this.depth + DrawVariables.elementSubBlockWidth
            ) * this@DrawService.drawParams.scale

    val scaledSubBlockHeight = this.drawParams.scale * DrawVariables.elementSubBlockHeight
    val scaledSubBlockWidth = this.drawParams.scale * DrawVariables.elementSubBlockWidth

    val newBlockOffsetX = (DrawVariables.spacingWidth + DrawVariables.elementSubBlockWidth * 2)
}