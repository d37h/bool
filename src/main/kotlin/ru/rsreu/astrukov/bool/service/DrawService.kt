package ru.rsreu.astrukov.bool.service

import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Line
import javafx.scene.shape.Rectangle
import javafx.scene.shape.StrokeType
import javafx.scene.text.Font
import javafx.scene.text.Text
import ru.rsreu.astrukov.bool.model.Coordinates
import ru.rsreu.astrukov.bool.model.DrawParams
import ru.rsreu.astrukov.bool.model.DrawVariables
import ru.rsreu.astrukov.bool.model.element.*


class DrawService(
        var drawParams: DrawParams
) {

    fun draw(element: BoolElement, node: Pane, offsetY: Double = 0.0) {
        //todo:  add line for top elem

        if (element.coordinates == null) {
            return
        }

        if (element.parent == null) {

            val line = Line(
                    element.coordinates!!.getPosX(),
                    element.coordinates!!.getPosY(offsetY) + this@DrawService.scaledSubBlockHeight,
                    element.coordinates!!.getPosX() - this@DrawService.scaledSubBlockWidth * 2,
                    element.coordinates!!.getPosY(offsetY) + this@DrawService.scaledSubBlockHeight
            )

            node.children.add(line)

        }

        if (element is BoolElementWithChildren) {
            element.firstChild?.let {
                val coordOffset = offsetY - DrawVariables.connectorOffset
                val isTerminalFunction = element.firstChild is BoolElementFunction && (element.firstChild as BoolElementFunction).type.isTerminal()
                val endOffsetY = if (element.firstChild is BoolElementVariable || isTerminalFunction) DrawVariables.connectorOffset else 0.0
                drawConnection(
                        startCoords = element.coordinates!!,
                        endCoords = element.firstChild!!.coordinates!!,
                        startOffsetY = coordOffset,
                        endOffsetY = offsetY + endOffsetY,
                        node = node
                )

                draw(it, node, offsetY+ endOffsetY)
            }
            element.secondChild?.let {
                val offset = offsetY + (element.firstChild?.coordinates?.elementHeight ?: 0.0
                        ) + DrawVariables.spacingHeight
                val coordOffset = offsetY +  DrawVariables.connectorOffset
                val isTerminalFunction = element.firstChild is BoolElementFunction && (element.firstChild as BoolElementFunction).type.isTerminal()
                val endOffsetY = if (element.firstChild is BoolElementVariable || isTerminalFunction) DrawVariables.connectorOffset else 0.0
                drawConnection(
                        element.coordinates!!,
                        endCoords = element.secondChild!!.coordinates!!,
                        startOffsetY = coordOffset,
                        endOffsetY = offset - endOffsetY,
                        node = node
                )

                draw(it, node, offset - endOffsetY)

            }
        }

        when (element) {
            is BoolElementBlock -> {
                drawBIP(element, element.coordinates!!, node, offsetY)
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
        val isTerminalFunction = root is BoolElementVariable || ( root is BoolElementFunction && root.type.isTerminal())

        if (!isTerminalFunction && root is BoolElementWithChildren) {
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

//        root.coordinates = Coordinates(
//                depth = depth * newBlockOffsetX,
//                elementHeight = DrawVariables.elementSubBlockHeight * 2
//        )

        root.coordinates = Coordinates(
                depth = depth * newBlockOffsetX,
                elementHeight = if (isTerminalFunction) {
                    DrawVariables.elementSubBlockHeight
                } else  DrawVariables.elementSubBlockHeight * 2
        )
    }

    private fun drawBIP(element: BoolElementBlock, coordinates: Coordinates, node: Pane, offsetY: Double) {
        //todo: add AND and vars via text
        val mainRect = styleRect(Rectangle()).apply {
            this.x = coordinates.getPosX()
            this.y = coordinates.getPosY(offsetY)
            this.height = this@DrawService.scaledSubBlockHeight * 2
            this.width = this@DrawService.scaledSubBlockWidth + this@DrawService.drawParams.lineThickness
        }


        val text = Text(
                coordinates.getPosX() + this@DrawService.scaledSubBlockWidth * 2,
                coordinates.getPosY(offsetY) + this@DrawService.scaledSubBlockHeight,
                element.excludedVariable
        )
        styleText(text)

        val line = Line(
                coordinates.getPosX() + this@DrawService.scaledSubBlockWidth * 2,
                coordinates.getPosY(offsetY) + this@DrawService.scaledSubBlockHeight,
                coordinates.getPosX() + this@DrawService.scaledSubBlockWidth * 2+ (this.drawParams.scale*DrawVariables.fontSize/3),
                coordinates.getPosY(offsetY) + this@DrawService.scaledSubBlockHeight
                )


        val r1 = styleRect(Rectangle()).apply {
            this.x = coordinates.getPosX() + this@DrawService.scaledSubBlockWidth
            this.y = coordinates.getPosY(offsetY)
            height = this@DrawService.scaledSubBlockHeight + this@DrawService.drawParams.lineThickness
            width = this@DrawService.scaledSubBlockWidth
        }

        val r2 = styleRect(Rectangle()).apply {
            this.x = coordinates.getPosX() + this@DrawService.scaledSubBlockWidth
            this.y = coordinates.getPosY(offsetY + DrawVariables.elementSubBlockHeight)
            height = this@DrawService.scaledSubBlockHeight
            width = this@DrawService.scaledSubBlockWidth
        }


        val x = coordinates.getPosX() //+ 4
        val y = coordinates.getPosY(offsetY) //+ 32

        val textBip = Text(x, y, "EXCL")
        styleText(textBip)

        node.children.addAll(mainRect,  r1, r2, /*textBip*,*/ text, line)
    }

    private fun drawFunction(element: BoolElementFunction, coordinates: Coordinates, node: Pane, offsetY: Double) {
        //todo: implement separate drawings for each type
        val mainRect = styleRect(Rectangle()).apply {
            x = coordinates.getPosX()
            y = coordinates.getPosY(offsetY)
            height = this@DrawService.scaledSubBlockHeight * 2
            width = this@DrawService.scaledSubBlockWidth
        }

        val x = coordinates.getPosX()// + 4
        val y = coordinates.getPosY(offsetY)// + 32

        val text = Text(x, y, element.type.toString())
        styleText(text)

        node.children.addAll(mainRect /*,text*/)
    }

    private fun drawVariable(element: BoolElementVariable, coordinates: Coordinates, node: Pane, offsetY: Double) {

        val text = Text(
                coordinates.getPosX(),
                coordinates.getPosY(offsetY, false),
                element.variable + element.variable + element.variable
        )
        styleText(text)

        node.children.addAll(text)
    }

    private fun drawConnection(startCoords: Coordinates, endCoords: Coordinates, startOffsetY: Double, endOffsetY: Double, node: Pane) {
        val line1 = Line(
                startCoords.getTopConnectorPosXStart(),
                startCoords.getTopConnectorPosYStart(startOffsetY),
                startCoords.getTopConnectorPosXStart() + drawParams.scale * (
                        DrawVariables.elementSubBlockWidth + DrawVariables.spacingWidth / 2),
                startCoords.getTopConnectorPosYStart(startOffsetY)
        )

        val line2 = Line(
                startCoords.getTopConnectorPosXStart() + drawParams.scale * (
                        DrawVariables.elementSubBlockWidth + DrawVariables.spacingWidth / 2),
                startCoords.getTopConnectorPosYStart(startOffsetY),
                startCoords.getTopConnectorPosXStart() + drawParams.scale * (
                        DrawVariables.elementSubBlockWidth + DrawVariables.spacingWidth / 2),
                endCoords.getTopConnectorPosYStart(endOffsetY)
        )

        val line3 = Line(
                startCoords.getTopConnectorPosXStart() + drawParams.scale * (
                        DrawVariables.elementSubBlockWidth + DrawVariables.spacingWidth / 2),
                endCoords.getTopConnectorPosYStart(endOffsetY),
                startCoords.getTopConnectorPosXStart() + drawParams.scale * (
                        DrawVariables.elementSubBlockWidth + DrawVariables.spacingWidth),
                endCoords.getTopConnectorPosYStart(endOffsetY)
        )

        line1.stroke = Color.BLACK
        line2.stroke = Color.BLACK
        line3.stroke = Color.BLACK

        line1.strokeWidth = drawParams.lineThickness
        line2.strokeWidth = drawParams.lineThickness
        line3.strokeWidth = drawParams.lineThickness

        node.children.addAll(line1, line2, line3)
    }

    private fun styleText(text: Text): Text {
        text.x += this.drawParams.scale * DrawVariables.fontSize /2
        text.y += this.drawParams.scale * DrawVariables.fontSize /3

        text.fill = Color.BLACK
        text.font = Font("Courier New", this.drawParams.scale * DrawVariables.fontSize)

        return text
    }

    private fun Coordinates.getPosY(offsetY: Double, isBlock:Boolean? = true): Double = (
            this.elementHeight / 2 + offsetY - if (isBlock == true) DrawVariables.elementSubBlockHeight else 0.0
            ) * this@DrawService.drawParams.scale

    private fun Coordinates.getPosX() = (this.depth) * this@DrawService.drawParams.scale

    private fun Coordinates.getTopConnectorPosYStart(offsetY: Double) = (
            this.elementHeight / 2 + offsetY
            ) * this@DrawService.drawParams.scale

    private fun Coordinates.getTopConnectorPosXStart() = (
            this.depth + DrawVariables.elementSubBlockWidth
            ) * this@DrawService.drawParams.scale

    private fun styleRect(rectangle: Rectangle): Rectangle {
        return rectangle.apply {
            this.fill = Color.WHEAT
            this.stroke = Color.BLACK
            this.strokeType = StrokeType.INSIDE
        }
    }


    val scaledSubBlockHeight = this.drawParams.scale * DrawVariables.elementSubBlockHeight
    val scaledSubBlockWidth = this.drawParams.scale * DrawVariables.elementSubBlockWidth

    val newBlockOffsetX = (DrawVariables.spacingWidth + DrawVariables.elementSubBlockWidth * 2)
}