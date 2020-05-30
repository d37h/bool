package ru.rsreu.astrukov.bool.service

import javafx.scene.layout.Pane
import ru.rsreu.astrukov.bool.model.BoolFunction

fun getBf(pane: Pane) {

    val eqs = EquationSolver()
    val drawService = DrawService(3.0,3.0)

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

    drawService.draw(root, pane)
    val a = ""
}