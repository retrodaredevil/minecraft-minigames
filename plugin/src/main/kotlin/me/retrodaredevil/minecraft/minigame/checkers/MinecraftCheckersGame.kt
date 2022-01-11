package me.retrodaredevil.minecraft.minigame.checkers

import me.retrodaredevil.board.Position
import me.retrodaredevil.board.checkers.CheckersColor
import me.retrodaredevil.board.checkers.CheckersGame
import me.retrodaredevil.board.checkers.CheckersMove
import me.retrodaredevil.minecraft.minigame.board.MinecraftBoardGame
import me.retrodaredevil.minecraft.minigame.board.WorldBoard
import me.retrodaredevil.minecraft.minigame.chess.ChessPlacer
import org.bukkit.Material

class MinecraftCheckersGame(
        override val worldBoard: WorldBoard,
        private val redPlayer: CheckersPlayer,
        private val whitePlayer: CheckersPlayer,
) : MinecraftBoardGame {
//    private val placer = CheckersPlacer.createDefault()
    private val placer = TorchCheckersPlacer.createDefault()
    override val isOver: Boolean
        get() = game.getResult() != null
    override val players: List<CheckersPlayer> = listOf(redPlayer, whitePlayer)

    val game = CheckersGame()

    fun getPlayer(color: CheckersColor) = if (color == CheckersColor.RED) redPlayer else whitePlayer

    override fun startGame() {
        updateBoard()
    }
    private fun updateBoard() {
        for (row in 0..7) {
            for (column in 0..7) {
                worldBoard.setPiece(placer, null, Position(column, row))
            }
        }
        for (pieceData in game.state.activePieces) {
            worldBoard.setPiece(placer, pieceData.piece, pieceData.position)
        }
    }

    fun move(move: CheckersMove) {
        val turnBefore = game.state.turn
        game.move(move)
        val turnAfter = game.state.turn

        updateBoard()

        val result = game.getResult()
        if (result == null) {
            if (turnBefore != turnAfter) {
                getPlayer(turnAfter).onTurnStart(this)
            } else {
                getPlayer(turnAfter).onTurnContinue(this)
            }
        } else {
            players.forEach { it.onGameEnd(result.winner) }
        }
    }
}