package me.retrodaredevil.board.chess

import me.retrodaredevil.board.BoardPiece
import me.retrodaredevil.board.Position

data class ChessPiece(
        val startingPosition: Position,
        val color: ChessColor,
        val type: PieceType,
        val promotedPawnPiece: ChessPiece? = null
) : BoardPiece {
}