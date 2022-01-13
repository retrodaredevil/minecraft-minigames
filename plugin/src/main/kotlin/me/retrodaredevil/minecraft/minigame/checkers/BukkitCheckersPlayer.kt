package me.retrodaredevil.minecraft.minigame.checkers

import me.retrodaredevil.board.Position
import me.retrodaredevil.board.checkers.CheckersColor
import me.retrodaredevil.board.checkers.CheckersPiece
import me.retrodaredevil.board.chess.ChessColor
import me.retrodaredevil.minecraft.minigame.board.BukkitBoardGamePlayer
import me.retrodaredevil.minecraft.minigame.board.MinecraftBoardGame
import me.retrodaredevil.minecraft.minigame.withPlayerIfOnline
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.util.*

class BukkitCheckersPlayer(
        override val color: CheckersColor,
        player: Player
) : CheckersPlayer, BukkitBoardGamePlayer {
    override val playerId: UUID = player.uniqueId

    var selectedPiece: CheckersPiece? = null
        private set

    override fun onPositionSelect(position: Position, player: Player, game: MinecraftBoardGame) {
        val checkersGame = game as MinecraftCheckersGame
        if (color != checkersGame.game.state.turn) {
            player.sendMessage("${ChatColor.AQUA}It is not your turn!!")
            return
        }

        val pieceRaw = checkersGame.game.state.getPieceAt(position)
        val pieceToSelect = if (pieceRaw != null && pieceRaw.color == color) pieceRaw else null
        if (pieceToSelect != null) {
            selectedPiece = pieceToSelect
            checkAutoSelect(checkersGame)
        } else {
            val selectedPiece = this.selectedPiece
            if (selectedPiece == null) {
                player.sendMessage("You must select a piece")
            } else {
                val possibleMoves = checkersGame.game.state.getPossibleMovesForColor(color)
                val move = possibleMoves.firstOrNull { it.piece == selectedPiece && it.endPosition == position}
                if (move == null) {
                    player.sendMessage("That is not a valid move")
                    this.selectedPiece = null
                } else {
                    player.sendMessage("Great move!")
                    checkersGame.move(move)
                    this.selectedPiece = null
                }
                checkAutoSelect(checkersGame)
            }
        }
    }

    override fun onTurnStart(checkersGame: MinecraftCheckersGame) {
        Bukkit.getPlayer(playerId)?.sendMessage("Your turn has started")
        checkAutoSelect(checkersGame)
    }

    override fun onTurnContinue(checkersGame: MinecraftCheckersGame) {
        Bukkit.getPlayer(playerId)?.sendMessage("You must jump again!")
        checkAutoSelect(checkersGame)
    }
    private fun checkAutoSelect(checkersGame: MinecraftCheckersGame) {
        val possibleMoves = checkersGame.game.state.getPossibleMovesForColor(color)
        val piecesToMove = possibleMoves.asSequence().map { it.piece }.toSet()
        if (piecesToMove.size == 1) {
            // auto select only available piece
            selectedPiece = piecesToMove.first()
        }
    }

    override fun onGameEnd(winner: CheckersColor?) {
        val player = Bukkit.getPlayer(playerId)
        if (player != null) {
            when (winner) {
                null -> {
                    player.sendMessage("The game ended in a draw!")
                }
                color -> {
                    player.sendMessage("You won!!")
                }
                else -> {
                    player.sendMessage("You lost!")
                }
            }
        }
    }

    override fun initiateForfeit(player: Player, game: MinecraftBoardGame) {
        val checkersGame = game as MinecraftCheckersGame
        checkersGame.game
    }

    override fun onForfeit(forfeitingPlayerColor: CheckersColor) {
        withPlayerIfOnline(playerId) { player ->
            if (color == forfeitingPlayerColor) {
                player.sendMessage("You have forfeited!")
            } else {
                player.sendMessage("The other player has forfeited! You have won!")
            }
        }
    }

    override fun onDraw() {
        withPlayerIfOnline(playerId) { player ->
            player.sendMessage("The match has ended in a draw!")
        }
    }
}