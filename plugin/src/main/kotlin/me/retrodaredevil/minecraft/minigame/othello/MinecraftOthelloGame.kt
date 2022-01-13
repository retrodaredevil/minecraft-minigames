package me.retrodaredevil.minecraft.minigame.othello

import me.retrodaredevil.board.Position
import me.retrodaredevil.board.chess.ChessColor
import me.retrodaredevil.board.othello.OthelloColor
import me.retrodaredevil.board.othello.OthelloGame
import me.retrodaredevil.board.othello.OthelloMove
import me.retrodaredevil.minecraft.minigame.board.MinecraftBoardGame
import me.retrodaredevil.minecraft.minigame.board.WorldBoard

class MinecraftOthelloGame(
        override val worldBoard: WorldBoard,
        val playerWhite: OthelloPlayer,
        val playerBlack: OthelloPlayer,
) : MinecraftBoardGame {
    init {
        require(playerWhite.color == OthelloColor.WHITE)
        require(playerBlack.color == OthelloColor.BLACK)
    }
    override val players: List<OthelloPlayer> = listOf(playerWhite, playerBlack)
    val game = OthelloGame()
    private val placer = OthelloPlacer.createDefault()
    private var forfeitingPlayerColor: OthelloColor? = null

    fun getPlayer(color: OthelloColor): OthelloPlayer = if (color == OthelloColor.WHITE) playerWhite else playerBlack

    override fun startGame() {
        updateBoard()
        getPlayer(game.turn).onTurnStart(this, false)
    }

    override val isOver: Boolean
        get() = game.getWinner() != null || forfeitingPlayerColor != null

    private fun updateBoard() {
        for (position in Position.ALL) {
            worldBoard.setPiece(placer, game.state.getTile(position), position)
        }
    }

    fun move(move: OthelloMove) {
        check(move.color == game.turn) { "It's not your turn! move: $move turn: ${game.turn}" }
        check(!isOver) { "The game is over! The winner is ${game.getWinner()}" }

        val beforeTurn = game.turn
        game.move(move)
        val afterTurn = game.turn

        updateBoard()
        val winner = game.getWinner()
        if (winner != null) {
            players.forEach { it.onGameEnd(winner) }
        } else {
            val skipped = beforeTurn == afterTurn
            if (skipped) {
                getPlayer(game.turn.opposite).onTurnSkip()
            }
            getPlayer(game.turn).onTurnStart(this, skipped)
        }
    }
    fun playerForfeit(playerColor: OthelloColor) {
        forfeitingPlayerColor = playerColor
        players.forEach { it.onForfeit(playerColor) }
    }
}