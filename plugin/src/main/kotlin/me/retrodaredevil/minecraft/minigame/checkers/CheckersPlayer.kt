package me.retrodaredevil.minecraft.minigame.checkers

import me.retrodaredevil.board.checkers.CheckersColor
import me.retrodaredevil.minecraft.minigame.board.BoardGamePlayer

interface CheckersPlayer : BoardGamePlayer {
    val color: CheckersColor

    fun onTurnStart(checkersGame: MinecraftCheckersGame)
    fun onTurnContinue(checkersGame: MinecraftCheckersGame)

    fun onGameEnd(winner: CheckersColor?)
}
