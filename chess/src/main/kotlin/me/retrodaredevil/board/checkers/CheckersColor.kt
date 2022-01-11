package me.retrodaredevil.board.checkers

enum class CheckersColor {
    /** White's first piece starts on A1*/
    WHITE,
    RED,
    ;
    val opposite: CheckersColor
        get() = if (this == WHITE) RED else WHITE
}