package me.retrodaredevil.minecraft.minigame.othello

import me.retrodaredevil.board.othello.OthelloColor
import me.retrodaredevil.minecraft.minigame.board.BoardGamePlayer

interface OthelloPlayer : BoardGamePlayer {
    val color: OthelloColor
    fun onTurnSkip()
    fun onTurnStart(othelloGame: MinecraftOthelloGame, wasOtherPlayerTurnSkipped: Boolean)
    fun onGameEnd(winner: OthelloColor)
    fun onDraw()
    fun onForfeit(forfeitingPlayerColor: OthelloColor)
}