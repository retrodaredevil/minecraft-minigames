package me.retrodaredevil.board.checkers

class CheckersGame {
    var state = CheckersState.createDefault()
        private set

    var turn = CheckersColor.RED
        private set

    fun move(move: CheckersMove) {
        state = state.move(move)
        if (!move.isJump || !state.getPossibleMovesForColor(turn).firstOrNull()!!.isJump) {
            // Turn is over only if the player has no jumps to do
            turn = turn.opposite
        }
    }
    fun getWinner(): CheckersColor? {
        var hasRed = false
        var hasWhite = false
        for (piece in state.activePieces) {
            if (piece.piece.color == CheckersColor.RED) {
                hasRed = true
            }
            if (piece.piece.color == CheckersColor.WHITE) {
                hasWhite = true
            }
            if (hasRed && hasWhite) {
                return null
            }
        }
        if (!hasRed) {
            return CheckersColor.WHITE
        }
        return CheckersColor.RED
    }
    data class Result(
            val winner: CheckersColor?,
            val isStalemateWin: Boolean,
    ) {

    }
}