package me.retrodaredevil.minecraft.minigame.board.listeners

import me.retrodaredevil.board.chess.*
import me.retrodaredevil.minecraft.minigame.board.BoardManager
import me.retrodaredevil.minecraft.minigame.chess.AiChessPlayer
import me.retrodaredevil.minecraft.minigame.chess.ChessPlacer
import me.retrodaredevil.minecraft.minigame.chess.MinecraftChessPlayer
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
import org.bukkit.event.player.PlayerJoinEvent

class BoardSelectListener(
        private val boardManager: BoardManager,
) : Listener {

    private var waitingPlayer: Player? = null

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        player.sendMessage("Heyo there");
        player.allowFlight = true
        player.health = player.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.value
        player.foodLevel = 20
    }
    @EventHandler
    fun onHealthChange(event: EntityDamageEvent) {
        val entity = event.entity
        if (entity is Player) {
            event.isCancelled = true
        }
    }
    @EventHandler
    fun onHungerChange(event: FoodLevelChangeEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        println("Block break by player: ${event.player}")
        if (event.player.gameMode == GameMode.CREATIVE) {
            val result = onPlayerSelectBlock(event.block, event.player)
            println("result: $result")
            if (result) {
                event.isCancelled = true
            }
        }
    }
    @EventHandler
    fun onBlockBreakStart(event: BlockDamageEvent) {
        println("Block break start by player: ${event.player}")
        if (event.player.gameMode != GameMode.CREATIVE) {
            val result = onPlayerSelectBlock(event.block, event.player)
            println("result: $result")
            if (result) {
                event.isCancelled = true
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