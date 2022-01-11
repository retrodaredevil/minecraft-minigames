package me.retrodaredevil.minecraft.minigame.chess

import me.retrodaredevil.board.chess.ChessColor
import me.retrodaredevil.board.chess.ChessGame
import me.retrodaredevil.board.chess.ChessPiece
import me.retrodaredevil.board.Position
import me.retrodaredevil.minecraft.minigame.board.BukkitBoardGamePlayer
import me.retrodaredevil.minecraft.minigame.board.MinecraftBoardGame
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.util.*

class BukkitChessPlayer(
        override val color: ChessColor,
        player: Player,
) : ChessPlayer, BukkitBoardGamePlayer {
    override val playerId: UUID = player.uniqueId
    private val playerName: String = player.name
    var selectedPiece: ChessPiece? = null
        private set

    override fun onTurnStart(chessGame: MinecraftChessGame) {
        val player = Bukkit.getPlayer(playerId)
        if (player == null) {
            println("Player: $playerName must not be online!")
            return
        }
        player.sendMessage("Your turn has started!")
        if (color in chessGame.game.state.getKingsInCheck()) {
            player.sendMessage("${ChatColor.RED}You are in check!")
        }
    }

    override fun onGameEnd(result: ChessGame.Result) {
        val player = Bukkit.getPlayer(playerId)
        if (player == null) {
            println("Player: $playerName must not be online!")
            return
        }
        if (result.isStalemate) {
            player.sendMessage("Stalemate! Game over!")
            player.sendTitle("Game over", "Stalemate", -1, -1, -1)
        } else if (result.winner == color) {
            player.sendMessage("You won!!!")
            player.sendTitle("Game over", "You won", -1, -1, -1)
        } else {
            player.sendMessage("You lost!!")
            player.sendTitle("Game over", "You lost", -1, -1, -1)
        }
    }

    override fun onPositionSelect(position: Position, player: Player, game: MinecraftBoardGame) {
        val chessGame = game as MinecraftChessGame

        if (color != chessGame.game.turn) {
            player.sendMessage("${ChatColor.AQUA}It is not your turn!!")
            return
        }
        val pieceRaw = chessGame.game.state.getPieceAt(position)
        val pieceToSelect = if (pieceRaw != null && pieceRaw.color == color) pieceRaw else null
        if (pieceToSelect != null) {
            selectedPiece = pieceToSelect
        } else {
            val selectedPiece = this.selectedPiece
            if (selectedPiece == null) {
                player.sendMessage("You must select a piece")
            } else {
                val moves = chessGame.game.state.getPossibleMoves(selectedPiece)
                val move = moves.firstOrNull { it.endPosition == position }
                if (move == null) {
                    player.sendMessage("That is not a valid move")
                    this.selectedPiece = null
                } else {
                    player.sendMessage("Great move!")
                    chessGame.move(move)
                    this.selectedPiece = null
                }
            }
        }
    }
}