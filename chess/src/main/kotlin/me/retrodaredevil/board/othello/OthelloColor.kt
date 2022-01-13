package me.retrodaredevil.board.othello

import me.retrodaredevil.board.BoardPiece

enum class OthelloColor : BoardPiece {
    WHITE,
    BLACK,
    ;
    val opposite: OthelloColor
        get() = if (this == WHITE) BLACK else WHITE
}