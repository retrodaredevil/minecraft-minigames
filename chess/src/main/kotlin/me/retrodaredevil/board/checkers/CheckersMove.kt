package me.retrodaredevil.board.checkers

import me.retrodaredevil.board.Position

data class CheckersMove(
        val piece: CheckersPiece,
        val startPosition: Position,
        val endPosition: Position,
        val jumpedPiece: CheckersPiece?,
        val jumpedPiecePosition: Position?,
        val promoteToKing: Boolean,
) {
    init {
        require(jumpedPiece == null || piece.color != jumpedPiece.color) { "You cannot jump your own color!" }
        require(startPosition != endPosition)
    }
    val isJump: Boolean
        get() {
            return jumpedPiece != null
        }
}