package ru.rsreu.astrukov.bool.service

import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Line
import javafx.scene.shape.Rectangle
import javafx.scene.text.Font
import javafx.scene.text.Text
import ru.rsreu.astrukov.bool.model.*
import ru.rsreu.astrukov.bool.model.element.*

class DrawService(
        private val xMult: Double,
        private val yMult: Double
) {

    fun draw(element:BoolElement, node: Pane) {
        node.background = Background( BackgroundFill(Color.ALICEBLUE, null, null))

        if (element.drawParams != null) {

            when(element) {
                is BoolElementBlock -> {
                    drawBIP(element.drawParams!!, node)
                    element.firstChild?.let {
                        draw(it, node)
                        val line = Line(
                                element.drawParams!!.posX, element.drawParams!!.posY,
                                element.firstChild.drawParams!!.posX, element.firstChild.drawParams!!.posY
                        )
                        node.children.add(line)
                    }
                    element.secondChild?.let {
                        draw(it, node)
                        val line = Line(
                                element.drawParams!!.posX, element.drawParams!!.posY,
                                element.secondChild.drawParams!!.posX, element.secondChild.drawParams!!.posY
                        )
                        node.children.add(line)
                    }

                }
                is BoolElementFunction -> {
                    drawFunction(element, element.drawParams!!, node)
                    element.firstChild?.let {
                        draw(it, node)
                        val line = Line(
                                element.drawParams!!.posX, element.drawParams!!.posY,
                                element.firstChild.drawParams!!.posX, element.firstChild.drawParams!!.posY
                        )
                        node.children.add(line)
                    }
                    element.secondChild?.let {
                        draw(it, node)
                        val line = Line(
                                element.drawParams!!.posX, element.drawParams!!.posY,
                                element.secondChild.drawParams!!.posX, element.secondChild.drawParams!!.posY
                        )
                        node.children.add(line)
                    }
                }
                is BoolElementVariable ->
                    drawVariable(element, element.drawParams!!, node)
            }
        }
    }


    fun setCoordinates(root: BoolElement, depth: Int) {
        if (root is BoolElementBlock) {
            if (root.firstChild != null) {

                setCoordinates(root.firstChild, depth + 1)
            }

            if (root.secondChild != null) {
                setCoordinates(root.secondChild, depth + 1)
            }

            root.drawParams = DrawParams(
                    depth * (ELEMENT_BLOCK_DISTANCE_DEPTH + ELEMENT_SUBBLOCK_WIDTH*2),
                    ELEMENT_BLOCK_DISTANCE +
                            (root.secondChild?.drawParams?.posY ?: 0.0) +
                            (root.firstChild?.drawParams?.posY ?: 0.0)
            )

            return
        }

        if (root is BoolElementFunction) {
            if (root.firstChild != null) {

                setCoordinates(root.firstChild, depth + 1)
            }

            if (root.secondChild != null) {
                setCoordinates(root.secondChild, depth + 1)
            }

            root.drawParams = DrawParams(
                    depth * (ELEMENT_BLOCK_DISTANCE_DEPTH + ELEMENT_SUBBLOCK_WIDTH*2),
                    ELEMENT_BLOCK_DISTANCE +
                            (root.secondChild?.drawParams?.posY ?: 0.0) +
                            (root.firstChild?.drawParams?.posY ?: 0.0)
            )

            return
        }

        root.drawParams = DrawParams(
                depth * (ELEMENT_BLOCK_DISTANCE_DEPTH + ELEMENT_SUBBLOCK_WIDTH*2),
                ELEMENT_SUBBLOCK_WIDTH * 2
        )

    }

    private fun drawBIP(drawParams: DrawParams, node: Pane) {


            val blockHeight = drawParams.scale * ELEMENT_SUBBLOCK_HEIGHT
            val blockWidth = drawParams.scale * ELEMENT_SUBBLOCK_WIDTH

                val mainRect = styleRect(Rectangle())

                mainRect.x = drawParams.posX * xMult
                mainRect.y = drawParams.posY * yMult
                mainRect.height = (2 * blockHeight - drawParams.lineThickness)
                mainRect.width = (blockWidth)

                val firstChildRect = Rectangle().let { styleRect(it) }

                firstChildRect.x = drawParams.posX * xMult + blockWidth - drawParams.lineThickness
                firstChildRect.y = drawParams.posY * yMult
                firstChildRect.height = blockHeight
                firstChildRect.width = blockWidth

                val secondChildRect = Rectangle().let { styleRect(it) }

                secondChildRect.x = drawParams.posX * xMult + blockWidth - drawParams.lineThickness
                secondChildRect.y = drawParams.posY * yMult + blockHeight - drawParams.lineThickness
                secondChildRect.height = blockHeight
                secondChildRect.width = blockWidth

                node.children.addAll(listOf(mainRect, firstChildRect, secondChildRect))

    }

    private fun drawFunction(element: BoolElementFunction, drawParams: DrawParams, node: Pane) {

            val blockHeight = drawParams.scale * ELEMENT_SUBBLOCK_HEIGHT
            val blockWidth = drawParams.scale * ELEMENT_SUBBLOCK_WIDTH

            val mainRect = Rectangle().let { styleRect(it) }

            mainRect.x = drawParams.posX * xMult
            mainRect.y = drawParams.posY * yMult
            mainRect.height = (2 * blockHeight - drawParams.lineThickness)
            mainRect.width = (blockWidth)

            val expression = element.type.stringValue + " "+ element.firstChild?.variable + " "+ element.secondChild?.variable
            val text = Text(drawParams.posX, drawParams.posY, expression)
            text.rotate = 90.0
            text.fill = Color.BLACK
        text.font = Font("Verdana",  drawParams.scale * ELMENT_FONT_SIZE)

            node.children.addAll(listOf(mainRect, text))

    }

    private fun drawVariable(element: BoolElementVariable, drawParams: DrawParams, node: Pane) {


            val blockHeight = drawParams.scale * ELEMENT_SUBBLOCK_HEIGHT
            val blockWidth = drawParams.scale * ELEMENT_SUBBLOCK_WIDTH

            val mainRect = Rectangle().let { styleRect(it) }

            mainRect.x = drawParams.posX * xMult
            mainRect.y = drawParams.posY * yMult
            mainRect.height = (2 * blockHeight - drawParams.lineThickness)
            mainRect.width = (blockWidth)

            val text = Text(drawParams.posX, drawParams.posY, element.variable)
            text.rotate = 90.0
            text.fill = Color.BLACK

            node.children.addAll(listOf(mainRect, text))

    }

}