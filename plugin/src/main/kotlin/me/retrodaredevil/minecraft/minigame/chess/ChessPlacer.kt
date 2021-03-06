package me.retrodaredevil.minecraft.minigame.chess

import me.retrodaredevil.board.chess.ChessColor
import me.retrodaredevil.board.chess.ChessPiece
import me.retrodaredevil.board.chess.PieceType
import me.retrodaredevil.minecraft.minigame.BlockCoordinate
import me.retrodaredevil.minecraft.minigame.board.ClearPiecePlacer
import me.retrodaredevil.minecraft.minigame.board.FlatDirection
import me.retrodaredevil.minecraft.minigame.board.PiecePlacer
import me.retrodaredevil.minecraft.minigame.board.util.getCenter
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
        ClearPiecePlacer.place(null, world, lowerLeftCorner, forwardDirection, tileWidth)
        val center = getCenter(lowerLeftCorner, forwardDirection, tileWidth)

        if (piece != null) {
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
                    val state = block2.state // https://bukkit.org/threads/rotating-blocks-using-the-directional-interface.406815/
                    state.blockData = (block2.state.blockData as Directional).apply {
                        facing = if (piece.color == ChessColor.WHITE) face else face.oppositeFace
                    }
                    state.update()
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