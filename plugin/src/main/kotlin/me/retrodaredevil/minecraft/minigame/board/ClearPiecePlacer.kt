package me.retrodaredevil.minecraft.minigame.board

import me.retrodaredevil.board.BoardPiece
import me.retrodaredevil.minecraft.minigame.BlockCoordinate
import org.bukkit.Material
import org.bukkit.World

object ClearPiecePlacer : PiecePlacer<BoardPiece> {
    private const val MAX_HEIGHT = 8

    override fun place(piece: BoardPiece?, world: World, lowerLeftCorner: BlockCoordinate, forwardDirection: FlatDirection, tileWidth: Int) {
        val rightDirection = forwardDirection.rotateRight()
        for (rightOffset in 0 until tileWidth) {
            for (forwardOffset in 0 until tileWidth) {
                val x = lowerLeftCorner.x + rightDirection.x * rightOffset + forwardDirection.x * forwardOffset
                val z = lowerLeftCorner.z + rightDirection.z * rightOffset + forwardDirection.z * forwardOffset
                for (yOffset in 1..MAX_HEIGHT) {
                    val y = lowerLeftCorner.y + yOffset
                    world.getBlockAt(x, y, z).setType(Material.AIR, false)
                }
            }
        }
    }
}