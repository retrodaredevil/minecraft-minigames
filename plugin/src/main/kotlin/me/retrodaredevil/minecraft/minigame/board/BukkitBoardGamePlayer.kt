package me.retrodaredevil.minecraft.minigame.board

import me.retrodaredevil.board.Position
import org.bukkit.entity.Player
import java.util.*

interface BukkitBoardGamePlayer : BoardGamePlayer {
    val playerId: UUID


    /**
     * A method that should be called when the player selects a tile/position. The implemention can
     * decide what selecting or clicking the specific tile makes them do.
     *
     * This is called by a listener and is specific to implementations of a [BoardGamePlayer] that are controlled
     * by a [Player].
     *
     * Note: [game] must be the game that this player is in. If it is not or if it is a different implementation,
     * a [ClassCastException] may be thrown.
     */
    fun onPositionSelect(position: Position, player: Player, game: MinecraftBoardGame)

    /**
     * Called by [me.retrodaredevil.minecraft.minigame.board.listeners.BoardSelectListener].
     * The implementation should tell [game] that this player has forfeited.
     */
    fun initiateForfeit(player: Player, game: MinecraftBoardGame)
}