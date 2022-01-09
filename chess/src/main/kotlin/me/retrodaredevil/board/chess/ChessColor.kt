package me.retrodaredevil.board.chess

enum class ChessColor {
    WHITE,
    BLACK,
    ;
    val opposite: ChessColor
        get() = if (this == WHITE) BLACK else WHITE
}