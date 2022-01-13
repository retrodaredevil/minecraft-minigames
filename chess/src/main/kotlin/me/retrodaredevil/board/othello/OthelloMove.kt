package me.retrodaredevil.board.othello

import me.retrodaredevil.board.Position

data class OthelloMove(
        val color: OthelloColor,
        val position: Position,
        val flipPositions: List<Position>
) {
    init {
        require(flipPositions.isNotEmpty()) { "flipPositions cannot be empty!" }
    }
}