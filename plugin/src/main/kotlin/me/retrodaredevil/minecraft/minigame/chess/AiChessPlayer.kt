package me.retrodaredevil.minecraft.minigame.chess

import me.retrodaredevil.board.chess.ChessColor
import me.retrodaredevil.board.chess.ChessGame

class AiChessPlayer(override val color: ChessColor) : ChessPlayer {
    override fun onTurnStart(chessGame: MinecraftChessGame) {
        check(chessGame.game.turn == color) { "It must be our turn!" }

        val moves = chessGame.game.state.getPossibleMovesForColor(color)
        check(moves.isNotEmpty()) { "We have no possible moves!" }
        val move = moves.random()
        chessGame.move(move)
    }

    override fun onGameEnd(result: ChessGame.Result) {
    }
}