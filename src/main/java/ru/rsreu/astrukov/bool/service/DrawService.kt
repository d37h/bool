package ru.rsreu.astrukov.bool.service

import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.scene.text.Font
import javafx.scene.text.Text
import ru.rsreu.astrukov.bool.model.*
import ru.rsreu.astrukov.bool.model.element.*

class DrawService() {

    var pixelsPerUnitWidth: Double = 1.0

    fun draw(element: BoolElement, node: Pane, offsetY: Double = 0.0) {
//        node.background = Background(BackgroundFill(Color.ALICEBLUE, null, null))

        if (element.drawParams != null) {

            when (element) {
                is BoolElementBlock -> {
                    drawBIP(element.drawParams!!, node, offsetY)
                }
                is BoolElementFunction -> {
                    drawFunction(element, element.drawParams!!, node, offsetY)
                }
                is BoolElementVariable -> {
                    drawVariable(element, element.drawParams!!, node, offsetY)
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
                    draw(it, node, offsetY + (element.firstChild?.drawParams?.posY ?: 0.0))
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

            root.drawParams = DrawParams(
                    depth * (ELEMENT_BLOCK_DISTANCE_DEPTH + ELEMENT_SUBBLOCK_WIDTH * 2),
                    ELEMENT_BLOCK_DISTANCE +
                            (root.secondChild?.drawParams?.posY ?: 0.0) +
                            (root.firstChild?.drawParams?.posY ?: 0.0)
            )

            return
        }


        root.drawParams = DrawParams(
                depth * (ELEMENT_BLOCK_DISTANCE_DEPTH + ELEMENT_SUBBLOCK_WIDTH * 2),
                ELEMENT_SUBBLOCK_WIDTH * 2
        )
    }

    private fun drawBIP(drawParams: DrawParams, node: Pane, offsetX: Double) {
        val blockHeight = drawParams.scale * ELEMENT_SUBBLOCK_HEIGHT
        val blockWidth = drawParams.scale * ELEMENT_SUBBLOCK_WIDTH

        val posX = (drawParams.posX) * drawParams.scale
        val posY = drawParams.getPosY(offsetX)

        val mainRect = styleRect(Rectangle())

        mainRect.x = posX
        mainRect.y = posY
        mainRect.height = (2 * blockHeight - drawParams.lineThickness)
        mainRect.width = (blockWidth)

        val firstChildRect = Rectangle().let { styleRect(it) }

        firstChildRect.x = posX + blockWidth - drawParams.lineThickness
        firstChildRect.y = posY
        firstChildRect.height = blockHeight
        firstChildRect.width = blockWidth

        val secondChildRect = Rectangle().let { styleRect(it) }

        secondChildRect.x = posX + blockWidth - drawParams.lineThickness
        secondChildRect.y = posY + blockHeight - drawParams.lineThickness
        secondChildRect.height = blockHeight
        secondChildRect.width = blockWidth

        node.children.addAll(listOf(mainRect, firstChildRect, secondChildRect))

    }

    private fun drawFunction(element: BoolElementFunction, drawParams: DrawParams, node: Pane, offsetX: Double) {
        val blockHeight = drawParams.scale * ELEMENT_SUBBLOCK_HEIGHT
        val blockWidth = drawParams.scale * ELEMENT_SUBBLOCK_WIDTH

        val posX = (drawParams.posX) * drawParams.scale
        val posY = drawParams.getPosY(offsetX)

        val mainRect = Rectangle().let { styleRect(it) }

        mainRect.x = posX
        mainRect.y = posY
        mainRect.height = (2 * blockHeight - drawParams.lineThickness)
        mainRect.width = (blockWidth)
        mainRect.fill = Color.ORANGE

        val expression = element.type.stringValue + " " + element.firstChild?.variable + " " + element.secondChild?.variable
        val text = Text(drawParams.posX, drawParams.posY, expression)
        text.rotate = 90.0
        text.fill = Color.BLACK
        text.font = Font("Verdana", drawParams.scale * ELEMENT_FONT_SIZE)

        node.children.addAll(listOf(mainRect, text))

    }

    private fun drawVariable(element: BoolElementVariable, drawParams: DrawParams, node: Pane, offsetX: Double) {
        val blockHeight = drawParams.scale * ELEMENT_SUBBLOCK_HEIGHT
        val blockWidth = drawParams.scale * ELEMENT_SUBBLOCK_WIDTH

        val posX = (drawParams.posX) * drawParams.scale
        val posY = drawParams.getPosY(offsetX)

        val mainRect = Rectangle().let { styleRect(it) }
        mainRect.fill = Color.RED

        mainRect.x = posX
        mainRect.y = posY
        mainRect.height = (2 * blockHeight - drawParams.lineThickness)
        mainRect.width = (blockWidth)

        val text = Text(drawParams.posX, drawParams.posY, element.variable)
        text.rotate = 90.0
        text.fill = Color.BLACK

        node.children.addAll(listOf(mainRect, text))

    }

    private fun DrawParams.getPosY(offsetY: Double) = ((this.posY)/2 + offsetY- ELEMENT_SUBBLOCK_HEIGHT) * this.scale

}