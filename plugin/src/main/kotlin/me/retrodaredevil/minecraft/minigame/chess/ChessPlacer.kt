package me.retrodaredevil.minecraft.minigame.chess

import me.retrodaredevil.board.chess.ChessColor
import me.retrodaredevil.board.chess.ChessPiece
import me.retrodaredevil.board.chess.PieceType
import me.retrodaredevil.minecraft.minigame.BlockCoordinate
import me.retrodaredevil.minecraft.minigame.board.FlatDirection
import me.retrodaredevil.minecraft.minigame.board.PiecePlacer
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.data.Directional

class ChessPlacer(
        private val whiteMaterial: Material,
        private val blackMaterial: Material,
) : PiecePlacer<ChessPiece> {
    override fun place(piece: ChessPiece?, world: World, lowerLeftCorner: BlockCoordinate, forwardDirection: FlatDirection, tileWidth: Int) {
        if (tileWidth % 2 == 0) {
            throw UnsupportedOperationException("ChessPlacer does not support even tileWidths! tileWidth: $tileWidth")
        }
        val center = run {
            val offset = (tileWidth - 1) / 2
            val rightDirection = forwardDirection.rotateRight()
            val xMultiplier = forwardDirection.x + rightDirection.x
            val zMultiplier = forwardDirection.z + rightDirection.z
            val offsetX = xMultiplier * offset
            val offsetZ = zMultiplier * offset
            BlockCoordinate(lowerLeftCorner.x + offsetX, lowerLeftCorner.y, lowerLeftCorner.z + offsetZ)
        }
        if (piece == null) {
            for (offset in 1..3) {
                world.getBlockAt(center.x, center.y + offset, center.z).type = Material.AIR
            }
        } else {
            placeWoolAtBlock(world.getBlockAt(center.x, center.y + 1, center.z), piece.color)
            val block2 = world.getBlockAt(center.x, center.y + 2, center.z)
            val block3 = world.getBlockAt(center.x, center.y + 3, center.z)
            when (piece.type) {
                PieceType.PAWN -> {
                    block2.type = Material.AIR
                    block3.type = Material.AIR
                }
                PieceType.ROOK -> {
                    block2.type = Material.STONE_BRICKS
                    block3.type = Material.AIR
                }
                PieceType.BISHOP -> {
                    block2.type = Material.STONE_BRICK_WALL
                    block3.type = Material.AIR
                }
                PieceType.KNIGHT -> {
                    block2.type = Material.LECTERN
                    block3.type = Material.AIR

                    val face = getWhiteBlockFace(forwardDirection)
                    (block2.state.blockData as Directional).facing = if (piece.color == ChessColor.WHITE) face else face.oppositeFace
                    block2.state.update()
                }
                PieceType.QUEEN -> {
                    block2.type = Material.STONE_BRICK_WALL
                    block3.type = Material.DIAMOND_BLOCK
                }
                PieceType.KING -> {
                    block2.type = Material.STONE_BRICK_WALL
                    block3.type = Material.GOLD_BLOCK
                }
            }
        }
    }
    private fun placeWoolAtBlock(block: Block, chessColor: ChessColor) {
        val material = when (chessColor) {
            ChessColor.WHITE -> whiteMaterial
            ChessColor.BLACK -> blackMaterial
        }
        block.type = material
    }
    private fun getWhiteBlockFace(forwardDirection: FlatDirection): BlockFace {
        return if (forwardDirection.x == 1) {
            BlockFace.EAST
        } else if (forwardDirection.x == -1) {
            BlockFace.WEST
        } else if (forwardDirection.z == 1) {
            BlockFace.SOUTH
        } else {
            BlockFace.NORTH
        }
    }
}