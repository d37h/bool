package ru.rsreu.astrukov.bool.service

import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.scene.text.Font
import javafx.scene.text.Text
import ru.rsreu.astrukov.bool.model.*
import ru.rsreu.astrukov.bool.model.element.*

class DrawService() {

    var drawParams = DrawParams(scale = 2.0)

    fun draw(element: BoolElement, node: Pane, offsetY: Double = 0.0) {

        if (element.coordinates != null) {

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
//                    val line = Line(
//                            element.drawParams!!.posX, element.drawParams!!.posY,
//                            it.drawParams!!.posX, it.drawParams!!.posY
//                    )
//                    node.children.add(line)
                }
                element.secondChild?.let {
                    draw(
                            it,
                            node,
                            offsetY + (element.firstChild?.coordinates?.elementWidth ?: 0.0)
                                    + DrawVariables.spacingHeight
                    )
//                    val line = Line(
//                            element.drawParams!!.posX, element.drawParams!!.posY,
//                            it.drawParams!!.posX, it.drawParams!!.posY
//                    )
//                    line.fill = Color.AQUAMARINE
//                    line.stroke = Color.AQUAMARINE
//                    node.children.add(line)
                }
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
                    elementWidth = DrawVariables.spacingHeight +
                            (root.secondChild?.coordinates?.elementWidth ?: 0.0) +
                            (root.firstChild?.coordinates?.elementWidth ?: 0.0)
            )

            return
        }

        root.coordinates = Coordinates(
                depth = depth * newBlockOffsetX,
                elementWidth = DrawVariables.elementSubBlockHeight * 2
        )
    }

    private fun drawBIP(coordinates: Coordinates, node: Pane, offsetX: Double) {
//        val blockHeight = this.drawParams.scale * DrawVariables.elementSubBlockHeight
//        val blockWidth = this.drawParams.scale * DrawVariables.elementSubBlockWidth
//
//        val posX = (coordinates.posX) * this.drawParams.scale
//        val posY = coordinates.getPosY(offsetX)
//
//        val mainRect = styleRect(Rectangle())
//
//        mainRect.x = posX
//        mainRect.y = posY
//        mainRect.height = (2 * blockHeight - this.drawParams.lineThickness)
//        mainRect.width = (blockWidth)
//
//        val firstChildRect = Rectangle().let { styleRect(it) }
//
//        firstChildRect.x = posX + blockWidth - this.drawParams.lineThickness
//        firstChildRect.y = posY
//        firstChildRect.height = blockHeight
//        firstChildRect.width = blockWidth
//
//        val secondChildRect = Rectangle().let { styleRect(it) }
//
//        secondChildRect.x = posX + blockWidth - this.drawParams.lineThickness
//        secondChildRect.y = posY + blockHeight - this.drawParams.lineThickness
//        secondChildRect.height = blockHeight
//        secondChildRect.width = blockWidth
//
//        node.children.addAll(listOf(mainRect, firstChildRect, secondChildRect))

        val mainRect = styleRect(Rectangle()).apply {
            x = coordinates.getPosX()
            y = coordinates.getPosY(offsetX)
            height = this@DrawService.scaledSubBlockHeight * 2
            width = this@DrawService.scaledSubBlockWidth
            fill = Color.WHEAT
        }

        node.children.addAll(listOf(mainRect))
    }

    private fun drawFunction(element: BoolElementFunction, coordinates: Coordinates, node: Pane, offsetY: Double) {

        val mainRect = styleRect(Rectangle())

        mainRect.x = coordinates.getPosX()
        mainRect.y = coordinates.getPosY(offsetY)
        mainRect.height = this@DrawService.scaledSubBlockHeight * 2
        mainRect.width = this@DrawService.scaledSubBlockWidth
        mainRect.fill = Color.ORANGE

        node.children.addAll(listOf(mainRect))

//        val expression = element.type.stringValue + " " + element.firstChild?.variable + " " + element.secondChild?.variable
//        val text = Text(coordinates.posX, coordinates.posY, expression)
//        text.rotate = 90.0
//        text.fill = Color.BLACK
//        text.font = Font("Verdana", this.drawParams.scale * ELEMENT_FONT_SIZE)

//        node.children.addAll(listOf(mainRect, text))

    }

    private fun drawVariable(element: BoolElementVariable, coordinates: Coordinates, node: Pane, offsetY: Double) {
//        val blockHeight = this.drawParams.scale * DrawVariables.elementSubBlockHeight
//        val blockWidth = this.drawParams.scale * DrawVariables.elementSubBlockWidth
//
//        val posX = (coordinates.posX) * this.drawParams.scale
//        val posY = coordinates.getPosY(offsetY)
//
//        val mainRect = Rectangle().let { styleRect(it) }
//        mainRect.fill = Color.RED
//
//        mainRect.x = posX
//        mainRect.y = posY
//        mainRect.height = (2 * blockHeight - this.drawParams.lineThickness)
//        mainRect.width = (blockWidth)
//
//        val text = Text(coordinates.posX, coordinates.posY, element.variable)
//        text.rotate = 90.0
//        text.fill = Color.BLACK
//
//        node.children.addAll(listOf(mainRect, text))

        val mainRect = styleRect(Rectangle()).apply {
            x = coordinates.getPosX()
            y = coordinates.getPosY(offsetY)
            height = this@DrawService.scaledSubBlockHeight * 2
            width = this@DrawService.scaledSubBlockWidth
            fill = Color.RED
        }

        node.children.addAll(listOf(mainRect))
    }

    private fun styleText(text: Text): Text {
        text.rotate = 90.0
        text.fill = Color.BLACK
        text.font = Font("Verdana", this.drawParams.scale * DrawVariables.fontSize)

        return text
    }

    private fun Coordinates.getPosY(offsetY: Double): Double {
//        val offset = if (offsetY == 0.0) -DrawVariables.elementSubBlockHeight*2
//        else offsetY

        return ((
                this.elementWidth) /2 + offsetY //-DrawVariables.elementSubBlockHeight*2
                )* this@DrawService.drawParams.scale
    }


    private fun Coordinates.getPosX() = (this.depth) * this@DrawService.drawParams.scale

    val scaledSubBlockHeight = this.drawParams.scale * DrawVariables.elementSubBlockHeight
    val scaledSubBlockWidth = this.drawParams.scale * DrawVariables.elementSubBlockWidth

    val newBlockOffsetX = (DrawVariables.spacingWidth + DrawVariables.elementSubBlockWidth * 2)
}