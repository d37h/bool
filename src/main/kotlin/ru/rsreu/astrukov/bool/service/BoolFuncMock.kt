package ru.rsreu.astrukov.bool.service

import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import ru.rsreu.astrukov.bool.model.BoolFunction
import ru.rsreu.astrukov.bool.model.DrawParams
import ru.rsreu.astrukov.bool.model.element.ext.toMatrix
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@ExperimentalTime
fun getBf(pane: Pane) {

    val eqs = EquationSolver()
    val drawParams = DrawParams(scale = 1.0)
    val drawService = DrawService(drawParams)

    val bf = BoolFunction(
            listOf(
                    BoolFunction.VariableGroup(listOf("x1", "!x2", "!x3", "x5")),
                    BoolFunction.VariableGroup(listOf("!x1", "x3", "!x5")),
                    BoolFunction.VariableGroup(listOf("x1", "x3", "!x4", "!x5")),
                    BoolFunction.VariableGroup(listOf("x1", "!x3", "x2", "x5"))
            )
    )

    val bf2 = BoolFunction(
            listOf(
                    BoolFunction.VariableGroup(listOf("x1", "!x2", "x3", "x4", "x5", "!x6")),
                    BoolFunction.VariableGroup(listOf("x1", "x2", "!x3", "x4", "!x5", "x6")),
                    BoolFunction.VariableGroup(listOf("x1", "x2", "x3", "!x4", "!x5", "!x6"))
            )
    )

    val bigBf = BoolFunction(
            listOf(
                    BoolFunction.VariableGroup(listOf("x1", "!x2", "!x3", "x5")),
                    BoolFunction.VariableGroup(listOf("x1", "x10", "!x3", "!x5")),
                    BoolFunction.VariableGroup(listOf("x1", "!x2", "x6", "!x5", "!x9", "x4")),
                    BoolFunction.VariableGroup(listOf("x10", "x2", "x7", "!x5")),
                    BoolFunction.VariableGroup(listOf("x9", "x2", "x7", "!x10")),
                    BoolFunction.VariableGroup(listOf("x1", "x8", "!x6", "x5", "!x2", "!x3", "x10")),
                    BoolFunction.VariableGroup(listOf("x1", "!x4", "x3", "x10", "x9", "x7")),
                    BoolFunction.VariableGroup(listOf("!x10", "!x2", "!x9", "x4", "x1", "!x8")),
                    BoolFunction.VariableGroup(listOf("x1", "!x9", "!x7", "x5")),
                    BoolFunction.VariableGroup(listOf("x1", "!x2", "!x3", "x4", "!x6", "!x7", "!x8")),
                    BoolFunction.VariableGroup(listOf("x1", "x5", "!x3", "x5", "x6")),
                    BoolFunction.VariableGroup(listOf("x7", "!x2", "x6", "!x5", "!x4")),
                    BoolFunction.VariableGroup(listOf("!x1", "x2", "x7", "!x5")),
                    BoolFunction.VariableGroup(listOf("x9", "x2", "x7", "!x10", "!x4", "!x5")),
                    BoolFunction.VariableGroup(listOf("x1", "x8", "!x3", "x5")),
                    BoolFunction.VariableGroup(listOf("x4", "!x9", "x3", "x10", "x8", "x5")),
                    BoolFunction.VariableGroup(listOf("x10", "!x2", "!x9", "x4")),
                    BoolFunction.VariableGroup(listOf("x1", "!x9", "!x7", "x5", "!x8", "x3", "x10"))
            )
    )

    val matr = bf.toMatrix()

    val simplifiedBf = eqs.simplify(bf)
//
//    val stdDur = measureTime {
//       EquationSolver().solve(simplifiedBf, SolveMode.STANDART)
//    }


    val openClDur = measureTime {
        val root1 = EquationSolver().solve(simplifiedBf, SolveMode.OPENCL)
    }
    println("openClDur: "+   openClDur.inMilliseconds)
//
//    val openClDurBig = measureTime {
//     eqs.solve(bigBf, SolveMode.OPENCL)
//    }
//    val root1 = eqs.solve(bigBf, SolveMode.OPENCL)
    val root = eqs.solve(simplifiedBf, SolveMode.OPENCL)

    drawService.setCoordinates(root, 1)
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