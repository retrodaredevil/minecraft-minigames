package me.retrodaredevil.minecraft.minigame.othello

import me.retrodaredevil.board.Position
import me.retrodaredevil.board.othello.OthelloColor
import me.retrodaredevil.minecraft.minigame.board.BukkitBoardGamePlayer
import me.retrodaredevil.minecraft.minigame.board.MinecraftBoardGame
import me.retrodaredevil.minecraft.minigame.withPlayerIfOnline
import org.bukkit.entity.Player
import java.util.*

class BukkitOthelloPlayer(
        override val color: OthelloColor,
        player: Player,
) : OthelloPlayer, BukkitBoardGamePlayer {
    override val playerId: UUID = player.uniqueId
    override fun onPositionSelect(position: Position, player: Player, game: MinecraftBoardGame) {
        val othelloGame = game as MinecraftOthelloGame
        val moves = othelloGame.game.state.getPossibleMoves(color)
        val move = moves.firstOrNull { it.position == position }
        if (move == null) {
            player.sendMessage("That is not a valid move")
        } else {
            player.sendMessage("Great move!")
            game.move(move)
        }

    }

    override fun initiateForfeit(player: Player, game: MinecraftBoardGame) {
        game as MinecraftOthelloGame
        game.playerForfeit(color)
    }

    override fun onTurnSkip() {
        withPlayerIfOnline(playerId) { player ->
            player.sendMessage("Your turn was skipped because you have no possible moves.")
        }
    }

    override fun onTurnStart(othelloGame: MinecraftOthelloGame, wasOtherPlayerTurnSkipped: Boolean) {
        withPlayerIfOnline(playerId) { player ->
            player.sendMessage("You turn has begun!")
        }
    }

    override fun onGameEnd(winner: OthelloColor) {
        withPlayerIfOnline(playerId) { player ->
            if (winner == color) {
                player.sendMessage("You won!")
            } else {
                player.sendMessage("You lost!")
            }
        }
    }

    override fun onDraw() {
        withPlayerIfOnline(playerId) { player ->
            player.sendMessage("The game ended in a draw!")
        }
    }

    override fun onForfeit(forfeitingPlayerColor: OthelloColor) {
        withPlayerIfOnline(playerId) { player ->
            if (forfeitingPlayerColor == color) {
                player.sendMessage("You have forfeited!")
            } else {
                player.sendMessage("Your opponent has forfeited! You won!")
            }
        }
    }

}