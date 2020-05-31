package ru.rsreu.astrukov.bool.model

object DrawVariables {

    const val elementSubBlockWidth = 16.0
    const val elementSubBlockHeight = 8.0

    const val spacingHeight = 32.0
    const val spacingWidth = 8.0

    const val connectorDiameter = 2.0
    const val connectorOffset = 2.0

    const val fontSize = 6
}

data class Coordinates(
        val depth: Double,
        val elementWidth: Double
)

data class DrawParams(
        var lineThickness: Double = 1.0,
        var scale: Double = 1.0
)