package me.retrodaredevil.board.othello

class OthelloGame {

    var state = OthelloState.createDefault()
        private set
    var turn = OthelloColor.BLACK
        private set

    fun move(move: OthelloMove) {
        require(move.color == turn)
        state = state.move(move)
        if (state.getPossibleMoves(turn.opposite).isNotEmpty()) {
            turn = turn.opposite // only have the other player move if they have available moves
        }
    }
    fun getWinner(): OthelloColor? {
        if (state.getPossibleMoves(OthelloColor.WHITE).isEmpty() && state.getPossibleMoves(OthelloColor.BLACK).isEmpty()) {
            return state.getTileCount().advantage ?: error("Neither color has an advantage!")
        }
        return null
    }
}