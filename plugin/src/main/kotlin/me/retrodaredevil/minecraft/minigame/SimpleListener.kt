package me.retrodaredevil.minecraft.minigame

import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.player.PlayerJoinEvent

class SimpleListener : Listener {
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
}