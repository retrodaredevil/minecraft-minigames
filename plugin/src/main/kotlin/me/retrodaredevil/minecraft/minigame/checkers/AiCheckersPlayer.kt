package me.retrodaredevil.minecraft.minigame.checkers

import me.retrodaredevil.board.checkers.CheckersColor

class AiCheckersPlayer(
        override val color: CheckersColor
) : CheckersPlayer {
    override fun onTurnStart(checkersGame: MinecraftCheckersGame) {
        val moves = checkersGame.game.state.getPossibleMovesForColor(color)
        val move = moves.random()
        checkersGame.move(move)
    }

    override fun onTurnContinue(checkersGame: MinecraftCheckersGame) {
        onTurnStart(checkersGame)
    }

    override fun onGameEnd(winner: CheckersColor?) {
    }

    override fun onDraw() {
    }

    override fun onForfeit(forfeitingPlayerColor: CheckersColor) {
    }

}
