package ru.rsreu.astrukov.bool.service

import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Line
import ru.rsreu.astrukov.bool.model.BoolFunction
import ru.rsreu.astrukov.bool.model.DrawParams
import ru.rsreu.astrukov.bool.model.element.ext.toMatrix

fun getBf(pane: Pane) {

    val eqs = EquationSolver()
    val drawService = DrawService()

    val bf = BoolFunction(
            listOf(
                    BoolFunction.VariableGroup(listOf("x1", "!x2", "!x3", "x5")),
                    BoolFunction.VariableGroup(listOf("!x1", "x3", "!x5")),
                    BoolFunction.VariableGroup(listOf("x1", "x3", "!x4", "!x5")),
                    BoolFunction.VariableGroup(listOf("x1", "!x3", "x2", "x5"))
            )
    )

    val matr = bf.toMatrix()

    val simplifiedBf = eqs.simplify(bf)
    val root = eqs.solve(simplifiedBf)


    drawService.setCoordinates(root, 1)

    val drawParams = DrawParams(scale = 2.0)

    val paneHeight = drawParams.scale * root.coordinates?.elementHeight!!

    pane.prefHeight = paneHeight
    pane.minHeight = paneHeight
    pane.background = Background(BackgroundFill(Color.GRAY, null, null), null, null)
    //todo:  separate drawparams from coords and move styleParams to service
    drawService.drawParams = drawParams

//    val netSize = 8.0
//    for (i in 1..25) {
//        val lineX = Line(0.0, i*netSize, 256.0, i*netSize)
//        lineX.stroke = if( i.rem(5) != 0 ) Color.DARKGREY else Color.BLACK
//        val lineY = Line(i*netSize, 0.0, i*netSize, 256.0)
//        lineY.stroke = if( i.rem(5) != 0 ) Color.DARKGREY else Color.BLACK
//        pane.children.addAll(lineX, lineY)
//    }

    drawService.draw(root, pane)
//    val rect = Rectangle(200.0,0.0, 50.0, 50.0)
//    val rect2 = Rectangle(200.0,200.0, 50.0, 50.0)
//    rect.fill = javafx.scene.paint.Color.RED
//    rect2.fill = javafx.scene.paint.Color.BLACK
//    pane.children.addAll(rect, rect2)

    val a = ""
}