package ru.rsreu.astrukov.bool

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage
import ru.rsreu.astrukov.bool.model.BoolElement
import ru.rsreu.astrukov.bool.model.BoolFunction
import ru.rsreu.astrukov.bool.service.EquationSolverKt

import java.util.Arrays


class BoolApplication : Application() {

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

        val root = eqs.solve(
                fn,
                Arrays.asList("x1", "x2", "x3", "x4", "x5"))

        val a = ""
    }

    companion object {


        @JvmStatic
        fun main(args: Array<String>) {
            launch(BoolApplication::class.java)
        }
    }

}
