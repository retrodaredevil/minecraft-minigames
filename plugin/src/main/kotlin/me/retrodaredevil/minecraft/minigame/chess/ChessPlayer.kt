package me.retrodaredevil.minecraft.minigame.chess

import me.retrodaredevil.board.chess.ChessColor
import me.retrodaredevil.board.chess.ChessGame

interface ChessPlayer {
    val color: ChessColor
    fun onTurnStart(chessGame: MinecraftChessGame)

    fun onGameEnd(result: ChessGame.Result)
}