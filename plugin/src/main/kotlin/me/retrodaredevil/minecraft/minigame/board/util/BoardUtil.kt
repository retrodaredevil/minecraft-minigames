package me.retrodaredevil.minecraft.minigame.board.util

import me.retrodaredevil.minecraft.minigame.BlockCoordinate
import me.retrodaredevil.minecraft.minigame.board.FlatDirection


fun getCenter(lowerLeftCorner: BlockCoordinate, forwardDirection: FlatDirection, tileWidth: Int): BlockCoordinate {
    if (tileWidth % 2 == 0) {
        throw UnsupportedOperationException("ChessPlacer does not support even tileWidths! tileWidth: $tileWidth")
    }
    val offset = (tileWidth - 1) / 2
    val rightDirection = forwardDirection.rotateRight()
    val xMultiplier = forwardDirection.x + rightDirection.x
    val zMultiplier = forwardDirection.z + rightDirection.z
    val offsetX = xMultiplier * offset
    val offsetZ = zMultiplier * offset
    return BlockCoordinate(lowerLeftCorner.x + offsetX, lowerLeftCorner.y, lowerLeftCorner.z + offsetZ)
}

