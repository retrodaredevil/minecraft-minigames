package me.retrodaredevil.board

/**
 * Represents an immutable object that represents a game piece on a 8x8 board.
 * [hashCode] and [equals] should be implemented.
 *
 * Uniqueness among pieces may vary between implementations. For instance, in chess, you can differentiate between
 * a rook that started on the left or right, but in othello, there is no differentiation between white tiles or between
 * black tiles.
 */
interface BoardPiece {
    override fun equals(other: Any?): Boolean
    override fun hashCode(): Int
}