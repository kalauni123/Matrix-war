package com.example.model

import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.sqrt

/**
 * 2D Grid Position on the tactical coordinate plane.
 * Coordinates are 0-indexed with origin at top-left or centered on grid.
 */
data class GridPos(val x: Int, val y: Int) {
    operator fun plus(other: GridPos): GridPos = GridPos(x + other.x, y + other.y)
    operator fun minus(other: GridPos): GridPos = GridPos(x - other.x, y - other.y)

    fun manhattanDistance(other: GridPos): Int = abs(x - other.x) + abs(y - other.y)

    fun euclideanDistance(other: GridPos): Float {
        val dx = (x - other.x).toFloat()
        val dy = (y - other.y).toFloat()
        return sqrt(dx * dx + dy * dy)
    }

    fun isWithinBounds(width: Int = 10, height: Int = 10): Boolean {
        return x in 0 until width && y in 0 until height
    }

    /**
     * Dot product with another position vector
     */
    fun dot(other: GridPos): Int = x * other.x + y * other.y

    /**
     * Normalized 2D vector for directional shielding and ricochet angles
     */
    fun toNormalized(): Pair<Float, Float> {
        val len = sqrt((x * x + y * y).toFloat())
        return if (len > 0f) Pair(x / len, y / len) else Pair(0f, 0f)
    }
}
