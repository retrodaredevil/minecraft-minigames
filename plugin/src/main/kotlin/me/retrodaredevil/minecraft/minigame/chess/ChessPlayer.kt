package me.retrodaredevil.minecraft.minigame.chess

import me.retrodaredevil.board.chess.ChessColor
import me.retrodaredevil.board.chess.ChessGame
import me.retrodaredevil.minecraft.minigame.board.BoardGamePlayer

interface ChessPlayer : BoardGamePlayer {
    val color: ChessColor
    fun onTurnStart(chessGame: MinecraftChessGame)

    fun onGameEnd(result: ChessGame.Result)
    fun onDraw()
    fun onForfeit(forfeitingPlayerColor: ChessColor)
}