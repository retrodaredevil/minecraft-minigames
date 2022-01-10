package me.retrodaredevil.minecraft.minigame.board.listeners

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import java.util.*

private inline fun runEventForInventory(event: InventoryClickEvent, targetInventory: Inventory, handleWithPlayer: (Player) -> Unit) {
    if (event.inventory == targetInventory) {
        event.isCancelled = true
        val player = event.whoClicked as? Player
        if (player == null) {
            // TODO log as warn
            System.err.println("event.whoClicked is not a player! event.whoClicked: ${event.whoClicked}")
            return
        }
        handleWithPlayer(player)
    }
}

class GameSelectListener : Listener {

    private val opponentSelectChoice = mutableMapOf<UUID, OpponentSelect>()

    fun onInventoryClick(event: InventoryClickEvent) {
        runEventForInventory(event, OpponentSelectInventory.inventory) { player ->
            val choice = when (event.slot) {
                OpponentSelectInventory.PLAYER_INDEX -> OpponentSelect.PLAYER
                OpponentSelectInventory.COMPUTER_INDEX -> OpponentSelect.COMPUTER
                else -> null
            }
            if (choice != null) {
                event.whoClicked.uniqueId
                opponentSelectChoice[player.uniqueId] = choice
            }
        }
    }

    object OpponentSelectInventory {
        const val PLAYER_INDEX = 0
        const val COMPUTER_INDEX = 1
        val inventory: Inventory by lazy {
            val inventory = Bukkit.createInventory(null, 18, "${ChatColor.AQUA}Opponent Select")
            inventory.setItem(PLAYER_INDEX, ItemStack(Material.PLAYER_HEAD).apply {
                itemMeta!!.setDisplayName("Another Player")
            })
            inventory.setItem(COMPUTER_INDEX, ItemStack(Material.COMMAND_BLOCK).apply {
                itemMeta!!.setDisplayName("Computer")
            })
            inventory
        }

    }
    enum class OpponentSelect {
        PLAYER,
        COMPUTER
    }
}