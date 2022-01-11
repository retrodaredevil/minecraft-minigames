package me.retrodaredevil.minecraft.minigame.board.implementations

import me.retrodaredevil.board.BoardPiece
import me.retrodaredevil.board.Position
import me.retrodaredevil.minecraft.minigame.BlockCoordinate
import me.retrodaredevil.minecraft.minigame.board.FlatDirection
import me.retrodaredevil.minecraft.minigame.board.PiecePlacer
import me.retrodaredevil.minecraft.minigame.board.WorldBoard
import org.bukkit.*
import org.bukkit.block.Block
import org.bukkit.entity.Player
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

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
        val lowerLeftCorner = getLowerLeftCorner(position)
        placer.place(piece, world, lowerLeftCorner, forwardDirection, tileWidth)
    }
    private fun getLowerLeftCorner(position: Position): BlockCoordinate {
        val rightDirection = forwardDirection.rotateRight()
        return BlockCoordinate(
                a1Coordinate.x + tileWidth * (position.columnIndex * rightDirection.x + position.rowIndex * forwardDirection.x),
                a1Coordinate.y,
                a1Coordinate.z + tileWidth * (position.columnIndex * rightDirection.z + position.rowIndex * forwardDirection.z),
        )
    }

    override fun highlightPosition(position: Position, player: Player, highlightType: WorldBoard.HighlightType) {
        val lowerLeftCorner = getLowerLeftCorner(position)
        val lowerLeftBlockBox = world.getBlockAt(lowerLeftCorner.x, lowerLeftCorner.y, lowerLeftCorner.z).boundingBox
        val rightDirection = forwardDirection.rotateRight()
        val xMultiplier = forwardDirection.x + rightDirection.x
        val zMultiplier = forwardDirection.z + rightDirection.z
        val centerX = lowerLeftBlockBox.centerX + (xMultiplier * (tileWidth / 2.0 - 0.5))
        val centerY = lowerLeftBlockBox.centerY + 0.6
        val centerZ = lowerLeftBlockBox.centerZ + (zMultiplier * (tileWidth / 2.0 - 0.5))

        val radius: Double
        val particle: Particle
        val data: Any?
        when (highlightType) {
            WorldBoard.HighlightType.SELECTED_PIECE -> {
                radius = tileWidth / 2.0
                particle = Particle.DRIP_LAVA
                data = null
            }
            WorldBoard.HighlightType.MOVE -> {
                radius = tileWidth / 4.0
                particle = Particle.FLAME
                data = null
            }
            WorldBoard.HighlightType.CAPTURE -> {
                radius = tileWidth / 2.4
                particle = Particle.REDSTONE
                data = Particle.DustOptions(Color.RED, 1.0f)
            }
        }
        for (i in 0 until 16) {
            val radians = i * PI / 8.0
            player.spawnParticle(
                    particle,
                    centerX + radius * cos(radians),
                    centerY,
                    centerZ + radius * sin(radians),
                    1,
                    0.0, 0.0, 0.0,
                     0.0,
                    data
            )
        }
    }
}