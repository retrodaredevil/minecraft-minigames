package me.retrodaredevil.board.chess

data class Position(
        val columnIndex: Int,
        val rowIndex: Int,
) {
    init {
        check(columnIndex >= 0) { "columnIndex must be positive" }
        check(columnIndex < 8) { "columnIndex must be < 8"}
        check(rowIndex >= 0) { "rowIndex must be positive" }
        check(rowIndex < 8) { "rowIndex must be < 8"}
    }

    val name = "${'A' + columnIndex}${1 + rowIndex}"


    companion object {
        fun ofOrNull(columnIndex: Int, rowIndex: Int): Position? = if(columnIndex !in 0..7 || rowIndex !in 0..7) null else Position(columnIndex, rowIndex)
    }
}