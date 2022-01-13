package me.retrodaredevil.minecraft.minigame.othello

import me.retrodaredevil.board.othello.OthelloColor

class AiOthelloPlayer(
        override val color: OthelloColor,
) : OthelloPlayer {
    override fun onTurnStart(othelloGame: MinecraftOthelloGame, wasOtherPlayerTurnSkipped: Boolean) {
        val moves = othelloGame.game.state.getPossibleMoves(color)
        val move = moves.random()
        othelloGame.move(move)
    }

    override fun onTurnSkip() {
    }
    override fun onGameEnd(winner: OthelloColor) {
    }
    override fun onDraw() {
    }
    override fun onForfeit(forfeitingPlayerColor: OthelloColor) {
    }
}