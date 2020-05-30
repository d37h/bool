package ru.rsreu.astrukov.bool.service

import com.sun.prism.paint.Color
import javafx.scene.layout.Pane
import ru.rsreu.astrukov.bool.model.BoolFunction
import javafx.scene.shape.Rectangle

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

    val simplifiedBf = eqs.simplify(bf)
    val root = eqs.solve(simplifiedBf)


    drawService.setCoordinates(root, 1)
    val paneHeight = root.drawParams?.scale ?: 1.0 * (root.drawParams?.posY ?: 128.0)
    val pixelsPerUnitWidth = root.drawParams?.scale ?: 1.0
    pane.prefHeight = paneHeight
    pane.minHeight = paneHeight
    //todo:  separate drawparams from coords and move styleParams to service
    drawService.pixelsPerUnitWidth = pixelsPerUnitWidth
    drawService.draw(root, pane)
//    val rect = Rectangle(200.0,0.0, 50.0, 50.0)
//    val rect2 = Rectangle(200.0,200.0, 50.0, 50.0)
//    rect.fill = javafx.scene.paint.Color.RED
//    rect2.fill = javafx.scene.paint.Color.BLACK
//    pane.children.addAll(rect, rect2)

    val a = ""
}