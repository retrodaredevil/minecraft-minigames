package me.retrodaredevil.board.checkers

class CheckersGame {
    var state = CheckersState.createDefault()
        private set

    fun move(move: CheckersMove) {
        state = state.move(move)
    }
    fun getResult(): Result? {
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
                // If both colors exist, then make sure that the current turn has moves to do
                if (state.getPossibleMovesForColor(state.turn).isEmpty()) {
                    return Result(state.turn.opposite, true)
                }
                return null
            }
        }
        if (!hasRed) {
            return Result(CheckersColor.WHITE, false)
        }
        return Result(CheckersColor.RED, false)
    }
    data class Result(
            val winner: CheckersColor,
            val isStalemate: Boolean,
    )
}