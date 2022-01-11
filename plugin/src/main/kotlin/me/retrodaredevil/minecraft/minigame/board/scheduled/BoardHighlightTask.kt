package me.retrodaredevil.minecraft.minigame.board.scheduled

import me.retrodaredevil.minecraft.minigame.board.BoardManager
import me.retrodaredevil.minecraft.minigame.board.WorldBoard
import me.retrodaredevil.minecraft.minigame.checkers.BukkitCheckersPlayer
import me.retrodaredevil.minecraft.minigame.checkers.MinecraftCheckersGame
import me.retrodaredevil.minecraft.minigame.chess.BukkitChessPlayer
import me.retrodaredevil.minecraft.minigame.chess.MinecraftChessGame
import org.bukkit.Bukkit

class BoardHighlightTask(
        private val boardManager: BoardManager
) : Runnable {
    override fun run() {
        for (boardGame in boardManager.games) {
            if (boardGame is MinecraftChessGame) {
                val chessPlayer = boardGame.getPlayer(boardGame.game.turn)
                if (chessPlayer is BukkitChessPlayer) {
                    val player = Bukkit.getPlayer(chessPlayer.playerId)
                    val selectedPiece = chessPlayer.selectedPiece
                    if (player != null && selectedPiece != null) { // player is online and they have a piece selected
                        val position = boardGame.game.state.getPiecePosition(selectedPiece)
                                ?: error("The piece didn't have a position! selectedPiece: $selectedPiece")
                        boardGame.worldBoard.highlightPosition(position, player, WorldBoard.HighlightType.SELECTED_PIECE)
                        for (move in boardGame.game.state.getPossibleMoves(selectedPiece)) {
                            val type = if (move.pieceCaptured != null) WorldBoard.HighlightType.CAPTURE else WorldBoard.HighlightType.MOVE
                            boardGame.worldBoard.highlightPosition(move.endPosition, player, type)
                        }
                    }
                }
            } else if (boardGame is MinecraftCheckersGame) {
                val checkersPlayer = boardGame.getPlayer(boardGame.game.state.turn)
                if (checkersPlayer is BukkitCheckersPlayer) {
                    val player = Bukkit.getPlayer(checkersPlayer.playerId)
                    val selectedPiece = checkersPlayer.selectedPiece
                    if (player != null && selectedPiece != null) {
                        val position = boardGame.game.state.getPiecePosition(selectedPiece)
                                ?: error("The piece didn't have a position! selectedPiece $selectedPiece")
                        boardGame.worldBoard.highlightPosition(position, player, WorldBoard.HighlightType.SELECTED_PIECE)
                        val possibleMoves = boardGame.game.state.getPossibleMovesForColor(checkersPlayer.color)
                        possibleMoves.filter { it.piece == selectedPiece }.forEach { move ->
                            if (move.jumpedPiece != null) {
                                boardGame.worldBoard.highlightPosition(move.jumpedPiecePosition!!, player, WorldBoard.HighlightType.CAPTURE)
                            }
                            boardGame.worldBoard.highlightPosition(move.endPosition, player, WorldBoard.HighlightType.MOVE)
                        }
                    }
                }
            }
        }
    }
}