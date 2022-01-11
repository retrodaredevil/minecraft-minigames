package me.retrodaredevil.board.checkers

import me.retrodaredevil.board.Position

data class PieceData(
        val piece: CheckersPiece,
        val position: Position,
)


data class CheckersState(
        val activePieces: List<PieceData>,
        val inactivePieces: List<CheckersPiece>,
) {

    fun getPieceAt(position: Position) = pieceAt(position)?.piece

    private fun pieceAt(position: Position): PieceData? {
        return activePieces.firstOrNull { it.position == position }
    }
    fun getPiecePosition(piece: CheckersPiece): Position? {
        return activePieces.firstOrNull { it.piece == piece }?.position
    }
    private fun getPossibleMoves(pieceData: PieceData): Moves {
        val regularMoves = mutableListOf<CheckersMove>()
        val jumpMoves = mutableListOf<CheckersMove>()

        val forwardDirection: Int
        val kingPromoteRowIndex: Int

        if (pieceData.piece.startingPosition.rowIndex < 4) {
            forwardDirection = 1
            kingPromoteRowIndex = 7
        } else {
            forwardDirection = -1
            kingPromoteRowIndex = 0
        }

        for (direction in if (pieceData.piece.isKing) intArrayOf(-1, 1) else intArrayOf(forwardDirection)) {
            val leftPosition = Position.ofOrNull(pieceData.position.columnIndex - 1, pieceData.position.rowIndex + direction)
            val rightPosition = Position.ofOrNull(pieceData.position.columnIndex + 1, pieceData.position.rowIndex + direction)
            val leftJumpEndPosition = Position.ofOrNull(pieceData.position.columnIndex - 2, pieceData.position.rowIndex + 2 * direction)
            val rightJumpEndPosition = Position.ofOrNull(pieceData.position.columnIndex + 2, pieceData.position.rowIndex + 2 * direction)

            for ((movePosition, jumpEndPosition) in arrayOf(
                    Pair(leftPosition, leftJumpEndPosition),
                    Pair(rightPosition, rightJumpEndPosition),
            )) {
                val targetPiece = movePosition?.let { pieceAt(it) }
                val jumpEndPiece = jumpEndPosition?.let { pieceAt(it) }

                if (targetPiece == null) {
                    // no piece to the left, so we can move there
                    if (movePosition != null) {
                        regularMoves.add(CheckersMove(
                                pieceData.piece, pieceData.position, movePosition,
                                jumpedPiece = null,
                                jumpedPiecePosition = null,
                                promoteToKing = !pieceData.piece.isKing && movePosition.rowIndex == kingPromoteRowIndex
                        ))
                    }
                } else if (jumpEndPosition != null && jumpEndPiece == null && targetPiece.piece.color != pieceData.piece.color) {
                    // There is a piece to the left that we can jump over
                    jumpMoves.add(CheckersMove(
                            pieceData.piece, pieceData.position, jumpEndPosition,
                            jumpedPiece = targetPiece.piece,
                            jumpedPiecePosition = movePosition,
                            promoteToKing = !pieceData.piece.isKing && movePosition.rowIndex == kingPromoteRowIndex
                    ))
                }
            }
        }
        return Moves(regularMoves, jumpMoves)
    }
    fun getPossibleMoves(piece: CheckersPiece): Moves {
        val pieceData = activePieces.firstOrNull { it.piece == piece} ?: error("Could not find piece: $piece in active pieces")
        return getPossibleMoves(pieceData)
    }
    fun getPossibleMovesForColor(color: CheckersColor): List<CheckersMove> {
        val moves: Moves = activePieces
                .filter { it.piece.color == color }
                .map { getPossibleMoves(it) }
                .reduce { moves1, moves2 -> moves1 + moves2 }
        if (moves.jumpMoves.isNotEmpty()) {
            return moves.jumpMoves
        }
        return moves.regularMoves
    }

    fun move(move: CheckersMove): CheckersState {
        val newActivePieces = activePieces.mapNotNull {
            if (move.jumpedPiece == it.piece) {
                null
            } else if (it.piece == move.piece) {
                if (move.promoteToKing) {
                    PieceData(CheckersPiece(move.piece.color, move.endPosition, originalPiece = move.piece), move.endPosition)
                } else {
                    PieceData(it.piece, move.endPosition)
                }
            } else {
                it
            }
        }
        val newInactivePieces = inactivePieces.toMutableList()
        if (move.jumpedPiece != null) {
            newInactivePieces.add(move.jumpedPiece)
        }
        if (move.promoteToKing) {
            newInactivePieces.add(move.piece)
        }
        return CheckersState(newActivePieces, newInactivePieces)
    }


    data class Moves(
            val regularMoves: List<CheckersMove>,
            val jumpMoves: List<CheckersMove>,
    ) {
        operator fun plus(moves: Moves): Moves {
            return Moves(regularMoves + moves.regularMoves, jumpMoves + moves.jumpMoves)
        }
    }

    companion object {
        fun createDefault(): CheckersState {
            val activePieces = mutableListOf<PieceData>()
            addRow(activePieces, 0, 0, CheckersColor.RED)
            addRow(activePieces, 1, 1, CheckersColor.RED)
            addRow(activePieces, 0, 2, CheckersColor.RED)

            addRow(activePieces, 1, 7, CheckersColor.WHITE)
            addRow(activePieces, 0, 6, CheckersColor.WHITE)
            addRow(activePieces, 1, 5, CheckersColor.WHITE)
            return CheckersState(activePieces, emptyList())
        }

        private fun addRow(activePieces: MutableList<in PieceData>, columnIndexStart: Int, rowIndex: Int, color: CheckersColor) {
            for (i in 0 until 4) {
                val position = Position(columnIndexStart + i * 2, rowIndex)
                activePieces.add(PieceData(CheckersPiece(color, position, null), position))
            }
        }
    }

}