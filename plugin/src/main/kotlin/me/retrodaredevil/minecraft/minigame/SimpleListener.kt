package me.retrodaredevil.minecraft.minigame

import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockFromToEvent
import org.bukkit.event.block.BlockPhysicsEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.player.PlayerGameModeChangeEvent
import org.bukkit.event.player.PlayerJoinEvent

/**
 * This listener contains logic for making the server a specific way. If we add more worlds that need different
 * logic later, we will have to change code in here.
 */
class SimpleListener : Listener {

    private fun updatePlayer(player: Player) {
        player.allowFlight = true
        player.health = player.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.value
        player.foodLevel = 20
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        player.sendMessage("Heyo there");
        updatePlayer(player)
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
    fun onBlockPlace(event: BlockPlaceEvent) {
        val player = event.player
        if (player.gameMode != GameMode.CREATIVE) {
            event.isCancelled = true
        }
    }
    @EventHandler
    fun onBlockDestroy(event: BlockBreakEvent) {
        val player = event.player
        if (player.gameMode != GameMode.CREATIVE) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onGameModeChange(event: PlayerGameModeChangeEvent) {
        // Changing game modes may remove the player's ability to fly, so let's make sure it is correct
        updatePlayer(event.player)
    }
    @EventHandler
    fun onBlockChange(event: BlockPhysicsEvent) {
        if (event.block.blockData.material == Material.GRASS_BLOCK && event.changedType == Material.DIRT) {
            event.isCancelled = true
        }
    }
}