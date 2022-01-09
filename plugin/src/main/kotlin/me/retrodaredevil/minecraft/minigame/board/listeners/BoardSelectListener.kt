package me.retrodaredevil.minecraft.minigame.board.listeners

import me.retrodaredevil.board.chess.*
import me.retrodaredevil.minecraft.minigame.board.BoardManager
import me.retrodaredevil.minecraft.minigame.chess.AiChessPlayer
import me.retrodaredevil.minecraft.minigame.chess.ChessPlacer
import me.retrodaredevil.minecraft.minigame.chess.MinecraftChessPlayer
import org.bukkit.GameMode
import org.bukkit.Material
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

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        event.player.sendMessage("Heyo there");
        event.player.allowFlight = true
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
        if (event.player.gameMode == GameMode.CREATIVE) {
            val result = onPlayerSelectBlock(event.block, event.player)
            if (result) {
                event.isCancelled = true
            }
        }
    }
    @EventHandler
    fun onBlockBreakStart(event: BlockDamageEvent) {
        if (event.player.gameMode != GameMode.CREATIVE) {
            val result = onPlayerSelectBlock(event.block, event.player)
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
        val chessGame = boardManager.getChessGame(worldBoard)
        if (chessGame == null) {
            if (player.gameMode == GameMode.CREATIVE) {
                // Players in creative mode cannot start games by clicking on blocks
                return false
            }
            boardManager.startGame(
                    worldBoard,
                    MinecraftChessPlayer(ChessColor.WHITE, player.uniqueId, player.name),
                    AiChessPlayer(ChessColor.BLACK)
            )
            player.sendMessage("You started a chess game with the computer! You are white.")
            return true
        }
        val chessPlayer = chessGame.players.filterIsInstance<MinecraftChessPlayer>().firstOrNull()
        if (chessPlayer != null) {
            chessPlayer.onPositionSelect(position, player, chessGame)
            return true
        }
        return false
    }
}