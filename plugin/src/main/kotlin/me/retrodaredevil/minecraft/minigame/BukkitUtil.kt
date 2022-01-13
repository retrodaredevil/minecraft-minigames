package me.retrodaredevil.minecraft.minigame

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*


inline fun withPlayerIfOnline(uniqueId: UUID, doWithPlayer: (Player) -> Unit) {
    val player = Bukkit.getPlayer(uniqueId)
    if (player != null) {
        doWithPlayer(player)
    }
}
