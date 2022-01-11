package me.retrodaredevil.board.checkers

import me.retrodaredevil.board.BoardPiece
import me.retrodaredevil.board.Position

data class CheckersPiece(
        val color: CheckersColor,
        val startingPosition: Position,
        /** The original non-king piece. If this field is non-null, then this piece is a king*/
        val originalPiece: CheckersPiece?
) : BoardPiece {

    val isKing: Boolean
        get() = originalPiece != null
}