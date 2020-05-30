package ru.rsreu.astrukov.bool.model

const val ELEMENT_SUBBLOCK_WIDTH = 8.0
const val ELEMENT_SUBBLOCK_HEIGHT = 12.0

val ELEMENT_BLOCK_DISTANCE = ELEMENT_SUBBLOCK_HEIGHT
val ELEMENT_BLOCK_DISTANCE_DEPTH = ELEMENT_SUBBLOCK_WIDTH * 2

const val ELEMENT_CONNECTOR_BORDER_OFFSET = 1.0
const val ELEMENT_CONNECTOR_DIAMETER = 1.0

const val ELMENT_FONT_SIZE = 6

data class Coordinates(
        val posX: Double,
        val posY: Double
)

data class DrawParams(
        var posX: Double,
        var posY: Double,
        var lineThickness: Double = 1.0,
        var scale: Double = 1.0

)