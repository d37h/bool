package ru.rsreu.astrukov.bool.service

import javafx.event.EventHandler
import javafx.scene.input.MouseEvent


private const val DOUBLE_CLICK_MAX_TIME_MS = 215

abstract class DoubleClickHandler: EventHandler<MouseEvent> {
    private var lastTime = 0L

    override fun handle(t: MouseEvent?) {
        val currentTime = System.currentTimeMillis()
        val diff = currentTime - lastTime

        if (diff > DOUBLE_CLICK_MAX_TIME_MS) {
            lastTime = currentTime
            return
        }

        performAction()
    }

    abstract fun performAction()

}