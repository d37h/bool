package ru.rsreu.astrukov.bool

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage
import ru.rsreu.astrukov.bool.model.*
import ru.rsreu.astrukov.bool.service.EquationSolverKt
import java.util.*


class BoolApplication : Application() {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(BoolApplication::class.java)
        }
    }

    override fun start(stage: Stage) {

        val sceneWithLayout = FXMLLoader.load<Parent>(javaClass.getResource("/scene.fxml"))

        val scene = Scene(sceneWithLayout, 800.0, 600.0)
        stage.title = "BoolApplication"
        stage.scene = scene
        stage.show()

        val eqs = EquationSolverKt()

        val bf = BoolFunction(
                listOf(
                        BoolFunction.VariableGroup(listOf("x1", "!x2", "!x3", "x5")),
                        BoolFunction.VariableGroup(listOf("!x1", "x3", "!x5")),
                        BoolFunction.VariableGroup(listOf("x1", "x3", "!x4", "!x5")),
                        BoolFunction.VariableGroup(listOf("x1", "!x3", "x2", "x5"))
                )
        )

        //todo: first group fwrong simplify
        val simplifiedBf = eqs.simplify(bf)

        val fn = "(x1 && !x2 && !x3 && x5) ||" +
                " (!x1 && x3 && !x5) || (x1 && x3 && !x4 && !x5)" +
                " || (x1 && x2 && !x3 && x5)"

        val root = eqs.solve(simplifiedBf)
        toMatrix(root, 0)
        val a = ""
    }

    fun toMatrix(root: BoolElement, depth: Int) {
        if (root is BoolElementBlock) {
            if (root.firstChild != null && root.firstChild.width == null) {

                toMatrix(root.firstChild, depth + 1)
            }

            if (root.secondChild != null && root.secondChild.width == null) {
                toMatrix(root.secondChild, depth + 1)
            }

            root.depth = ELEMENT_BLOCK_DISTANCE_DEPTH
            root.width = ELEMENT_BLOCK_DISTANCE + (root.secondChild?.width ?: 0.0) + (root.firstChild?.width ?: 0.0)

            return
        }

        root.depth = ELEMENT_BLOCK_DISTANCE_DEPTH
        root.width = ELEMENT_SUBBLOCK_WIDTH * 2

    }

    fun draw(root: BoolElement, unitWidth: Double) {

    }


}
