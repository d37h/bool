package ru.rsreu.astrukov.bool

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage


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

//        val ec =  OpenClService()
//        ec.run()
    }

}
