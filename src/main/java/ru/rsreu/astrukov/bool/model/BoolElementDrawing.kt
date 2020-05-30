package ru.rsreu.astrukov.bool.model

const val ELEMENT_SUBBLOCK_WIDTH = 8.0
const val ELEMENT_SUBBLOCK_HEIGHT = 4.0

val ELEMENT_BLOCK_DISTANCE = ELEMENT_SUBBLOCK_HEIGHT * 2
val ELEMENT_BLOCK_DISTANCE_DEPTH = ELEMENT_SUBBLOCK_WIDTH

const val ELEMENT_CONNECTOR_BORDER_OFFSET = 4.0
const val ELEMENT_CONNECTOR_DIAMETER = 4.0

const val ELEMENT_FONT_SIZE = 8

data class Coordinates(
        val posX: Double,
        val posY: Double
)

data class DrawParams(
        var lineThickness: Double = 1.0,
        var scale: Double = 1.0
)