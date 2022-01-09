package me.retrodaredevil.board.chess

import kotlin.contracts.contract

data class PieceData(
        val piece: ChessPiece,
        val position: Position,
        val movedYet: Boolean,
        val justMoved: Boolean,
)

data class ChessState(
        val activePieces: List<PieceData>,
        val inactivePieces: List<ChessPiece>,
) {

    fun getPieceAt(position: Position) = pieceAt(position)?.piece

    /**
     * @return All the possible moves of the given [piece]. May include invalid moves that put the player into check
     */
    private fun getPossibleMovesIgnoreCheck(piece: ChessPiece): List<ChessMove> {
        val pieceData = activePieces.firstOrNull { it.piece == piece} ?: error("Could not find piece: $piece in active pieces")
        val moves = mutableListOf<ChessMove>()
        val forwardDirection: Int = if(piece.color == ChessColor.WHITE) 1 else -1
        when (piece.type) {
            PieceType.PAWN -> {
                val leftDiagonal = Position.ofOrNull(pieceData.position.columnIndex - 1, pieceData.position.rowIndex + forwardDirection)
                val rightDiagonal = Position.ofOrNull(pieceData.position.columnIndex + 1, pieceData.position.rowIndex + forwardDirection)
                val forward = Position.ofOrNull(pieceData.position.columnIndex, pieceData.position.rowIndex + forwardDirection)
                val twoForward = Position.ofOrNull(pieceData.position.columnIndex, pieceData.position.rowIndex + 2 * forwardDirection)

                val moveType = run {
                    val isPromotion = if (piece.color == ChessColor.WHITE) {
                        pieceData.position.rowIndex == 6 // advancing will get pawn to end
                    } else {
                        pieceData.position.rowIndex == 1
                    }
                    if (isPromotion) ChessMove.Type.PAWN_PROMOTION else ChessMove.Type.REGULAR
                }

                if (!pieceData.movedYet && forward != null && twoForward != null && pieceAt(forward) == null && pieceAt(twoForward) == null) {
                    check(moveType == ChessMove.Type.REGULAR)
                    moves.add(ChessMove(piece, pieceData.position, twoForward, ChessMove.Type.REGULAR, pieceCaptured = null))
                }
                if (forward != null && pieceAt(forward) == null) {
                    moves.add(ChessMove(piece, pieceData.position, forward, moveType, pieceCaptured = null))
                }
                val attackingPawnEnPassantRowIndex = if (piece.color == ChessColor.WHITE) 4 else 3
                for (diagonalPosition in arrayOf(leftDiagonal, rightDiagonal)) { // iterate over both possible diagonal moves
                    if (diagonalPosition != null) {
                        val diagonalPositionPiece = pieceAt(diagonalPosition)
                        if (diagonalPositionPiece == null) {
                            // piece under diagonal has same column as the diagonal position, and same row as the attacking pawn's position
                            val pieceUnderDiagonal = pieceAt(Position(diagonalPosition.columnIndex, pieceData.position.rowIndex))
                            // This checks to see if the pawn is allowed to do the en passant move
                            if (pieceUnderDiagonal != null && pieceUnderDiagonal.piece.type == PieceType.PAWN
                                    && pieceUnderDiagonal.piece.color != piece.color
                                    && pieceUnderDiagonal.justMoved && pieceData.position.rowIndex == attackingPawnEnPassantRowIndex) {
                                check(moveType == ChessMove.Type.REGULAR)
                                moves.add(ChessMove(piece, pieceData.position, diagonalPosition, ChessMove.Type.REGULAR, pieceCaptured = pieceUnderDiagonal.piece))
                            }
                        } else if (diagonalPositionPiece.piece.color != piece.color) {
                            moves.add(ChessMove(piece, pieceData.position, diagonalPosition, moveType, pieceCaptured = diagonalPositionPiece.piece))
                        }
                    }
                }
            }
            PieceType.ROOK -> {
                addMovesForDirections(moves, arrayOf(Pair(1, 0), Pair(0, 1), Pair(-1, 0), Pair(0, -1)), pieceData)
            }
            PieceType.BISHOP -> {
                addMovesForDirections(moves, arrayOf(Pair(1, 1), Pair(-1, 1), Pair(1, -1), Pair(-1, -1)), pieceData)
            }
            PieceType.QUEEN -> {
                addMovesForDirections(moves, arrayOf(Pair(1, 0), Pair(0, 1), Pair(-1, 0), Pair(0, -1), Pair(1, 1), Pair(-1, 1), Pair(1, -1), Pair(-1, -1)), pieceData)
            }
            PieceType.KING -> {
                addMovesForDirections(moves, arrayOf(Pair(1, 0), Pair(0, 1), Pair(-1, 0), Pair(0, -1), Pair(1, 1), Pair(-1, 1), Pair(1, -1), Pair(-1, -1)), pieceData, oneMoveOnly = true)
                // TODO castle moves
            }
            PieceType.KNIGHT -> {
                addMovesForDirections(moves, arrayOf(Pair(2, 1), Pair(1, 2), Pair(-1, 2), Pair(-2, 1), Pair(-2, -1), Pair(-1, -2), Pair(1, -2), Pair(2, -1)), pieceData, oneMoveOnly = true)
            }
        }

        return moves
    }
    private fun pieceAt(position: Position): PieceData? {
        return activePieces.firstOrNull { it.position == position }
    }
    private fun addMovesForDirections(moves: MutableList<in ChessMove>, directions: Array<Pair<Int, Int>>, pieceData: PieceData, oneMoveOnly: Boolean = false) {
        val piece = pieceData.piece
        for (direction in directions) {
            var position: Position? = pieceData.position
            while (position != null) {
                position = Position.ofOrNull(position.columnIndex + direction.first, position.rowIndex + direction.second) ?: break
                val pieceAtPosition = pieceAt(position)
                if (pieceAtPosition != null && pieceAtPosition.piece.color == piece.color) {
                    break // we cannot capture our own piece
                }
                moves.add(ChessMove(piece, pieceData.position, position, ChessMove.Type.REGULAR, pieceCaptured = pieceAtPosition?.piece))
                if (pieceAtPosition != null || oneMoveOnly) {
                    break
                }
            }
        }
    }

    fun getPossibleMoves(piece: ChessPiece): List<ChessMove> {
        return getPossibleMovesIgnoreCheck(piece).filter {
            val stateAfter = move(it, if (it.moveType == ChessMove.Type.PAWN_PROMOTION) PieceType.QUEEN else null)
            // stop a given color from putting itself in check
            // stop a given color from castling out of check
            piece.color !in stateAfter.getKingsInCheck() && (it.moveType != ChessMove.Type.CASTLE || piece.color !in getKingsInCheck())
        }
    }
    fun getPossibleMovesForColor(color: ChessColor): List<ChessMove> {
        return activePieces.filter{ it.piece.color == color }
                .flatMap { getPossibleMoves(it.piece) }
    }

    fun move(move: ChessMove, pawnPromotionPieceType: PieceType?, debug: Boolean = false): ChessState {
        if (move.moveType == ChessMove.Type.PAWN_PROMOTION) {
            require(pawnPromotionPieceType != null) { "Must provide promotion piece for a pawn promotion move!" }
            require(pawnPromotionPieceType != PieceType.PAWN) { "Cannot choose pawn!" }
            require(pawnPromotionPieceType != PieceType.KING) { "Cannot choose king!" }
        } else {
            require(pawnPromotionPieceType == null) { "You cannot provide a pawn promotion piece type unless the move type supports it!" }
        }
        if (debug) {
            println("Doing move: $move")
        }

        val newActivePieces = activePieces.asSequence().mapNotNull {
            if (move.pieceCaptured != null && it.piece == move.pieceCaptured) {
                null
            } else if (it.piece == move.piece) {
                if (pawnPromotionPieceType == null) {
                    // Alter the position of a piece that just moved
                    PieceData(move.piece, move.endPosition, movedYet = true, justMoved = true)
                } else {
                    println("Promoting pawn: ${move.piece} with $pawnPromotionPieceType")
                    // We will make the pawn inactive below and will add a piece below
                    null
                }
            } else if (move.castleData != null && move.castleData.rookPiece == it.piece) {
                // Alter a rook's position that just castled
                PieceData(it.piece, move.castleData.rookEndingPosition, movedYet = true, justMoved = true) // TODO do we want justMoved true here?
            } else {
                it.copy(justMoved = false)
            }
        }.toMutableList()
        val newInactivePieces = inactivePieces.toMutableList()
        if (move.pieceCaptured != null) {
            newInactivePieces.add(move.pieceCaptured)
        }
        if (pawnPromotionPieceType != null) {
            val newPiece = ChessPiece(move.endPosition, move.piece.color, pawnPromotionPieceType, promotedPawnPiece = move.piece)
            newActivePieces.add(PieceData(newPiece, move.endPosition, movedYet = true, justMoved = true)) // add the new piece to the active pieces
            newInactivePieces.add(move.piece) // make the pawn inactive
            if (debug) {
                println("Yay pawn promotion! for newPiece: $newPiece")
                println(newActivePieces)
                println(newInactivePieces)
            }
        }
        return ChessState(newActivePieces, newInactivePieces)
    }
    fun getKingsInCheck(): Set<ChessColor> {
        val r = mutableSetOf<ChessColor>()
        for (pieceData in activePieces) {
            for (move in getPossibleMovesIgnoreCheck(pieceData.piece)) {
                if (move.pieceCaptured != null && move.pieceCaptured.type == PieceType.KING) {
                    r.add(move.pieceCaptured.color)
                }
            }
        }
        return r
    }
    fun getPiecePosition(piece: ChessPiece): Position? {
        return activePieces.firstOrNull { it.piece == piece }?.position
    }

    companion object {
        fun createDefault(): ChessState {
            val pieces = mutableListOf<ChessPiece>()
            for (i in 0 until 8) {
                pieces.add(ChessPiece(Position(i, 1), ChessColor.WHITE, PieceType.PAWN))
                pieces.add(ChessPiece(Position(i, 6), ChessColor.BLACK, PieceType.PAWN))
            }
            for (i in 0..1) {
                pieces.add(ChessPiece(Position(i * 7, 0), ChessColor.WHITE, PieceType.ROOK))
                pieces.add(ChessPiece(Position(i * 7, 7), ChessColor.BLACK, PieceType.ROOK))

                pieces.add(ChessPiece(Position(1 + i * 5, 0), ChessColor.WHITE, PieceType.KNIGHT))
                pieces.add(ChessPiece(Position(1 + i * 5, 7), ChessColor.BLACK, PieceType.KNIGHT))

                pieces.add(ChessPiece(Position(2 + i * 3, 0), ChessColor.WHITE, PieceType.BISHOP))
                pieces.add(ChessPiece(Position(2 + i * 3, 7), ChessColor.BLACK, PieceType.BISHOP))
            }
            pieces.add(ChessPiece(Position(3, 0), ChessColor.WHITE, PieceType.QUEEN))
            pieces.add(ChessPiece(Position(3, 7), ChessColor.BLACK, PieceType.QUEEN))

            pieces.add(ChessPiece(Position(4, 0), ChessColor.WHITE, PieceType.KING))
            pieces.add(ChessPiece(Position(4, 7), ChessColor.BLACK, PieceType.KING))
            return ChessState(pieces.map { PieceData(it, it.startingPosition, movedYet = false, justMoved = false) }, emptyList())
        }
    }
}