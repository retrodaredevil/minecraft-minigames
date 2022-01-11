package me.retrodaredevil.board.chess

import me.retrodaredevil.board.Position


data class ChessMove(
        val piece: ChessPiece,
        val startPosition: Position,
        val endPosition: Position,
        val moveType: Type,
        val pieceCaptured: ChessPiece?,
        val castleData: CastleData? = null,
) {
    init {
        require(startPosition != endPosition) { "You cannot stay still!" }
        if (moveType == Type.CASTLE) {
            require(pieceCaptured == null) { "A castle cannot capture pieces" }
            require(castleData != null) { "Must provide castle data!" }
        } else {
            require(castleData == null) { "Must use CASTLE type if you provide castle data!" }
        }
    }

    enum class Type {
        REGULAR,
        PAWN_PROMOTION,
        CASTLE
    }
    data class CastleData(
            val rookPiece: ChessPiece,
            val rookStartingPosition: Position,
            val rookEndingPosition: Position,
    )
}