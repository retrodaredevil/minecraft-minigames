package me.retrodaredevil.minecraft.minigame.board.scheduled

import me.retrodaredevil.minecraft.minigame.board.BoardManager
import me.retrodaredevil.minecraft.minigame.board.WorldBoard
import me.retrodaredevil.minecraft.minigame.chess.MinecraftChessPlayer
import org.bukkit.Bukkit

class BoardHighlightTask(
        private val boardManager: BoardManager
) : Runnable {
    override fun run() {
        for (chessGame in boardManager.chessGames) {
            val chessPlayer = chessGame.getPlayer(chessGame.game.turn)
            if (chessPlayer is MinecraftChessPlayer) {
                val player = Bukkit.getPlayer(chessPlayer.playerUuid)
                val selectedPiece = chessPlayer.selectedPiece
                if (player != null && selectedPiece != null) { // player is online and they have a piece selected
                    val position = chessGame.game.state.getPiecePosition(selectedPiece)
                            ?: error("The piece didn't have a position! selectedPiece: $selectedPiece")
                    chessGame.worldBoard.highlightPosition(position, player, WorldBoard.HighlightType.SELECTED_PIECE)
                    for (move in chessGame.game.state.getPossibleMoves(selectedPiece)) {
                        val type = if (move.pieceCaptured != null) WorldBoard.HighlightType.CAPTURE else WorldBoard.HighlightType.MOVE
                        chessGame.worldBoard.highlightPosition(move.endPosition, player, type)
                    }
                }
            }
        }
    }
}