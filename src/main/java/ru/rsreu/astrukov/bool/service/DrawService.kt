package ru.rsreu.astrukov.bool.service

import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
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
                    draw(it, node, offsetY + (element.firstChild?.coordinates?.posY ?: 0.0) + ELEMENT_BLOCK_DISTANCE)
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
                    depth * (ELEMENT_BLOCK_DISTANCE_DEPTH + ELEMENT_SUBBLOCK_WIDTH * 2),
                    ELEMENT_BLOCK_DISTANCE +
                            (root.secondChild?.coordinates?.posY ?: 0.0) +
                            (root.firstChild?.coordinates?.posY ?: 0.0)
            )

            return
        }


        root.coordinates = Coordinates(
                depth * (ELEMENT_BLOCK_DISTANCE_DEPTH + ELEMENT_SUBBLOCK_WIDTH * 2),
                ELEMENT_SUBBLOCK_WIDTH * 2
        )
    }

    private fun drawBIP(coordinates: Coordinates, node: Pane, offsetX: Double) {
//        val blockHeight = this.drawParams.scale * ELEMENT_SUBBLOCK_HEIGHT
//        val blockWidth = this.drawParams.scale * ELEMENT_SUBBLOCK_WIDTH
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
            height = this@DrawService.subBlockHeight * 2
            width = this@DrawService.subBlockWidth
            fill = Color.WHEAT
        }

        node.children.addAll(listOf(mainRect))
    }

    private fun drawFunction(element: BoolElementFunction, coordinates: Coordinates, node: Pane, offsetY: Double) {

        val mainRect = styleRect(Rectangle())

        mainRect.x = coordinates.getPosX()
        mainRect.y = coordinates.getPosY(offsetY)
        mainRect.height = this@DrawService.subBlockHeight * 2
        mainRect.width = this@DrawService.subBlockWidth
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
//        val blockHeight = this.drawParams.scale * ELEMENT_SUBBLOCK_HEIGHT
//        val blockWidth = this.drawParams.scale * ELEMENT_SUBBLOCK_WIDTH
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

        val yCoord = coordinates.getPosY(offsetY)
        val mainRect = styleRect(Rectangle()).apply {
            x = coordinates.getPosX()
            y = coordinates.getPosY(offsetY)
            height = this@DrawService.subBlockHeight * 2
            width = this@DrawService.subBlockWidth
            fill = Color.RED
        }

        node.children.addAll(listOf(mainRect))
    }

    private fun Coordinates.getPosY(offsetY: Double): Double {
        val offset = if (offsetY != 0.0)  {
            offsetY
        } else -ELEMENT_SUBBLOCK_HEIGHT*2

        return ((this.posY) / 2 + offset) * this@DrawService.drawParams.scale
    }


    private fun Coordinates.getPosX() = (this.posX) * this@DrawService.drawParams.scale

    val subBlockHeight = this.drawParams.scale * ELEMENT_SUBBLOCK_HEIGHT
    val subBlockWidth = this.drawParams.scale * ELEMENT_SUBBLOCK_WIDTH
}