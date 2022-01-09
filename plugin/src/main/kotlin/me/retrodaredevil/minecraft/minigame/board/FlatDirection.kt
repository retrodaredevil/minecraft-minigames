package me.retrodaredevil.minecraft.minigame.board

import kotlin.math.absoluteValue

/**
 * Represents the board orientation.
 * The (x, z) vector points in the direction of forward on the board
 */
data class FlatDirection(
        val x: Int,
        val z: Int,
) {
    init {
        check(x.absoluteValue == 1 && z == 0 || x == 0 && z.absoluteValue == 1)
    }

    fun rotateRight(): FlatDirection {
        return FlatDirection(-z, x)
    }
}