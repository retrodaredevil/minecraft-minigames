package me.retrodaredevil.minecraft.minigame.board.implementations

import me.retrodaredevil.board.BoardPiece
import me.retrodaredevil.board.chess.Position
import me.retrodaredevil.minecraft.minigame.BlockCoordinate
import me.retrodaredevil.minecraft.minigame.board.FlatDirection
import me.retrodaredevil.minecraft.minigame.board.PiecePlacer
import me.retrodaredevil.minecraft.minigame.board.WorldBoard
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.block.Block
import kotlin.math.absoluteValue

class SimpleWorldBoard(
        private val world: World,
        private val a1Coordinate: BlockCoordinate,
        /** The direction that white's pawns move in (increasing row index) */
        private val forwardDirection: FlatDirection,
        private val tileWidth: Int,
        private val maxHeight: Int,
) : WorldBoard {
    override fun getPosition(block: Block): Position? {
        if (block.world != world) {
            return null
        }
        if (block.y < a1Coordinate.y) {
            return null // selected block is below
        }
        if (block.y > a1Coordinate.y + maxHeight) {
            return null // select block is below the pieces
        }
        val rightDirection = forwardDirection.rotateRight()

        val xOffsetRaw = block.x - a1Coordinate.x
        val zOffsetRaw = block.z - a1Coordinate.z

        val columnIndexRaw = xOffsetRaw * rightDirection.x + zOffsetRaw * rightDirection.z
        val rowIndexRaw = xOffsetRaw * forwardDirection.x + zOffsetRaw * forwardDirection.z
        if (columnIndexRaw in 0 until tileWidth * 8 && rowIndexRaw in 0 until tileWidth * 8) {
            return Position(columnIndexRaw / tileWidth, rowIndexRaw / tileWidth)
        }
        return null
    }


    override fun <T : BoardPiece> setPiece(placer: PiecePlacer<T>, piece: T?, position: Position) {

        val rightDirection = forwardDirection.rotateRight()

        val lowerLeftCorner = BlockCoordinate(
                a1Coordinate.x + tileWidth * (position.columnIndex * rightDirection.x + position.rowIndex * forwardDirection.x),
                a1Coordinate.y,
                a1Coordinate.z + tileWidth * (position.columnIndex * rightDirection.z + position.rowIndex * forwardDirection.z),
        )
        placer.place(piece, world, lowerLeftCorner, forwardDirection, tileWidth)
    }
}