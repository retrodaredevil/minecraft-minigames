package me.retrodaredevil.minecraft.minigame.board.listeners

import me.retrodaredevil.minecraft.minigame.board.BoardManager
import me.retrodaredevil.minecraft.minigame.board.BukkitBoardGamePlayer
import me.retrodaredevil.minecraft.minigame.board.NewGameHandler
import org.bukkit.GameMode
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerAnimationEvent
import org.bukkit.event.player.PlayerAnimationType

class BoardSelectListener(
        private val boardManager: BoardManager,
        private val newGameHandler: NewGameHandler,
) : Listener {

    @EventHandler
    fun onPlayerPunch(event: PlayerAnimationEvent) {
        if (event.animationType == PlayerAnimationType.ARM_SWING) {
            val block = event.player.getTargetBlockExact(20)
            if (block != null) {
                onPlayerSelectBlock(block, event.player)
                // We don't really care about cancelling this event, but if we did, we could check the result of onPlayerSelectBlock
            }
        }
    }

    /**
     * @return true if the player has selected a block that is part of a board that they are playing on
     */
    private fun onPlayerSelectBlock(block: Block, player: Player): Boolean {
        val (worldBoard, position) = boardManager.getPosition(block) ?: return false
        val currentGame = boardManager.getGame(worldBoard)
        if (currentGame == null) {
            if (player.gameMode == GameMode.CREATIVE) {
                // Players in creative mode cannot start games by clicking on blocks
                return false
            }
            newGameHandler.onPlayerRequestBoard(worldBoard, player)
            return true
        }
        val chessPlayer = currentGame.players.filterIsInstance<BukkitBoardGamePlayer>().firstOrNull { it.playerId == player.uniqueId }
        if (chessPlayer != null) {
            chessPlayer.onPositionSelect(position, player, currentGame)
            return true
        }
        return false
    }
}