package me.retrodaredevil.minecraft.minigame.board.listeners

import me.retrodaredevil.board.chess.*
import me.retrodaredevil.minecraft.minigame.board.BoardManager
import me.retrodaredevil.minecraft.minigame.chess.AiChessPlayer
import me.retrodaredevil.minecraft.minigame.chess.ChessPlacer
import me.retrodaredevil.minecraft.minigame.chess.MinecraftChessPlayer
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockDamageEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.player.PlayerAnimationEvent
import org.bukkit.event.player.PlayerAnimationType
import org.bukkit.event.player.PlayerJoinEvent

class BoardSelectListener(
        private val boardManager: BoardManager,
) : Listener {

    private var waitingPlayer: Player? = null

    @EventHandler
    fun onPlayerPunch(event: PlayerAnimationEvent) {
        if (event.animationType == PlayerAnimationType.ARM_SWING) {
            val block = event.player.getTargetBlockExact(20)
            if (block != null) {
                val result = onPlayerSelectBlock(block, event.player)
                if (result) {
                    event.isCancelled = true
                }
            }
        }
    }

    /**
     * @return true if the player has selected a block that is part of a board that they are playing on
     */
    private fun onPlayerSelectBlock(block: Block, player: Player): Boolean {
        val (worldBoard, position) = boardManager.getPosition(block) ?: return false
        println("position: $position")
        val chessGame = boardManager.getChessGame(worldBoard)
        println("chessGame: $chessGame")
        if (chessGame == null) {
            if (player.gameMode == GameMode.CREATIVE) {
                // Players in creative mode cannot start games by clicking on blocks
                return false
            }
            if (Bukkit.getOnlinePlayers().size == 1) { // HARD code ability to play against AI
                player.sendMessage("Starting game against AI. You are white.")
                boardManager.startGame(
                        worldBoard,
                        MinecraftChessPlayer(ChessColor.WHITE, player),
                        AiChessPlayer(ChessColor.BLACK),
                )
                return true
            }
            val waitingPlayer = this.waitingPlayer
            if (waitingPlayer == null || !waitingPlayer.isOnline) {
                this.waitingPlayer = player
                player.sendMessage("You are now waiting")
                return true
            }
            if (waitingPlayer.uniqueId == player.uniqueId) {
                player.sendMessage("You are already waiting")
                return true
            }
            boardManager.startGame(
                    worldBoard,
                    MinecraftChessPlayer(ChessColor.WHITE, waitingPlayer),
                    MinecraftChessPlayer(ChessColor.BLACK, player),
            )
            waitingPlayer.sendMessage("Game is starting with ${player.name}. You are white.")
            player.sendMessage("Game is starting with $waitingPlayer. You are black")
            return true
        }
        val chessPlayer = chessGame.players.filterIsInstance<MinecraftChessPlayer>().firstOrNull { it.playerUuid == player.uniqueId }
        if (chessPlayer != null) {
            chessPlayer.onPositionSelect(position, player, chessGame)
            return true
        }
        return false
    }
}