package me.retrodaredevil.minecraft.minigame.board

import me.retrodaredevil.board.BoardPiece
import me.retrodaredevil.minecraft.minigame.BlockCoordinate
import org.bukkit.World

class PiecePlacerSwitch<PIECE : BoardPiece, COLOR>(
        private val pieceToColor: (PIECE) -> COLOR,
        private val color1: COLOR,
        private val placer1: PiecePlacer<PIECE>,
        private val color2: COLOR,
        private val placer2: PiecePlacer<PIECE>,
) : PiecePlacer<PIECE> {
    override fun place(piece: PIECE?, world: World, lowerLeftCorner: BlockCoordinate, forwardDirection: FlatDirection, tileWidth: Int) {
        if (piece == null) {
            ClearPiecePlacer.place(null, world, lowerLeftCorner, forwardDirection, tileWidth)
        } else {
            val placer = when (val color = pieceToColor(piece)) {
                color1 -> placer1
                color2 -> placer2
                else -> error("Unknown color: $color")
            }
            placer.place(piece, world, lowerLeftCorner, forwardDirection, tileWidth)
        }
    }
}