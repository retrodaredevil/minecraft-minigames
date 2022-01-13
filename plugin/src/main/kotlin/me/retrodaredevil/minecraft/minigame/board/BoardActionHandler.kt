package me.retrodaredevil.minecraft.minigame.board

import org.bukkit.entity.Player
import java.time.Duration
import java.util.*

/**
 * A player can type commands to alter the result of clicking on the board. For instance, if a player wants to
 * forfeit, then the next time they click on a board, they will be asked to confirm if they would like to forfeit on
 * that board.
 */
class BoardActionHandler {
    private val actionMap = mutableMapOf<UUID, Data>()

    fun getAction(player: Player): Action? {
        val data = actionMap[player.uniqueId]
        if (data == null || data.expireTimeNanos < System.nanoTime()) {
            return null
        }
        return data.action
    }

    fun clearAction(player: Player) {
        actionMap.remove(player.uniqueId)
    }

    fun configureAction(player: Player, action: Action) {
        actionMap[player.uniqueId] = Data(action, System.nanoTime() + Duration.ofSeconds(12).toNanos())
    }

    private class Data(
            val action: Action,
            val expireTimeNanos: Long,
    )
    enum class Action {
        FORFEIT,
        REQUEST_DRAW,

    }
}