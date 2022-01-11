package me.retrodaredevil.board.chess

class ChessGame {
    var state = ChessState.createDefault()
        private set

    var turn = ChessColor.WHITE
        private set

    fun move(move: ChessMove, pawnPromotionPieceType: PieceType?) {
        state = state.move(move, pawnPromotionPieceType)
        turn = turn.opposite
    }
    fun getResult(): Result? {
        val kingsInCheck = state.getKingsInCheck()
        check(turn.opposite !in kingsInCheck) { "The king in check cannot be the other color! kingsInCheck: $kingsInCheck turn: $turn" }

        val hasAnyMove = state.hasAnyMoves(turn)
        if (!hasAnyMove) {
            return Result(turn.opposite, kingsInCheck.isEmpty())
        }
        return null
    }

    data class Result(
            val winner: ChessColor,
            val isStalemate: Boolean,
    )
}