package me.retrodaredevil.minecraft.minigame.chess

import me.retrodaredevil.board.chess.ChessColor
import me.retrodaredevil.board.chess.ChessGame
import me.retrodaredevil.board.chess.ChessPiece
import me.retrodaredevil.board.chess.Position
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.block.Block
import org.bukkit.entity.Player
import java.util.*

class MinecraftChessPlayer(
        override val color: ChessColor,
        player: Player,
) : ChessPlayer {
    val playerUuid: UUID = player.uniqueId
    private val playerName: String = player.name
    private var selectedPiece: ChessPiece? = null

    override fun onTurnStart(chessGame: MinecraftChessGame) {
        val player = Bukkit.getPlayer(playerUuid)
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
        val player = Bukkit.getPlayer(playerUuid)
        if (player == null) {
            println("Player: $playerName must not be online!")
            return
        }
        if (result.isStalemate) {
            player.sendMessage("Stalemate! Game over!")
        } else if (result.winner == color) {
            player.sendMessage("You won!!!")
        } else {
            player.sendMessage("You lost!!")
        }
    }

    fun onPositionSelect(position: Position, player: Player, chessGame: MinecraftChessGame) {
        println("onPositionSelect!")
        if (color != chessGame.game.turn) {
            player.sendMessage("${ChatColor.AQUA}It is not your turn!!")
            return
        }
        val pieceRaw = chessGame.game.state.getPieceAt(position)
        val pieceToSelect = if (pieceRaw != null && pieceRaw.color == color) pieceRaw else null
        if (pieceToSelect != null) {
            selectedPiece = pieceToSelect
            println("selected: $selectedPiece")
        } else {
            val selectedPiece = this.selectedPiece
            if (selectedPiece == null) {
                player.sendMessage("You must select a piece")
            } else {
                val moves = chessGame.game.state.getPossibleMoves(selectedPiece)
                val move = moves.firstOrNull { it.endPosition == position }
                if (move == null) {
                    player.sendMessage("That is not a valid move")
                } else {
                    player.sendMessage("Great move!")
                    chessGame.move(move)
                    this.selectedPiece = null
                }
            }
        }
    }
}