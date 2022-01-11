package me.retrodaredevil.minecraft.minigame.board.listeners

import me.retrodaredevil.minecraft.minigame.board.NewGameHandler
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

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

class GameSelectListener(
        private val newGameHandler: NewGameHandler
) : Listener {


    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        runEventForInventory(event, OpponentSelectInventory.inventory) { player ->
            val choice = when (event.slot) {
                OpponentSelectInventory.PLAYER_INDEX -> NewGameHandler.OpponentType.PLAYER
                OpponentSelectInventory.COMPUTER_INDEX -> NewGameHandler.OpponentType.COMPUTER
                else -> null
            }
            if (choice != null) {
                newGameHandler.playerSelectOpponent(player, choice)
                player.openInventory(GameTypeSelectInventory.inventory)
            }
        }
        runEventForInventory(event, GameTypeSelectInventory.inventory) { player ->
            val choice = when (event.slot) {
                GameTypeSelectInventory.CHESS_INDEX -> NewGameHandler.GameType.CHESS
                GameTypeSelectInventory.CHECKERS_INDEX -> NewGameHandler.GameType.CHECKERS
                else -> null
            }
            if (choice != null) {
                newGameHandler.playerSelectGame(player, choice)
                player.closeInventory()
            }
        }
    }

    object OpponentSelectInventory {
        const val PLAYER_INDEX = 0
        const val COMPUTER_INDEX = 1
        val inventory: Inventory by lazy {
            val inventory = Bukkit.createInventory(null, 18, "${ChatColor.AQUA}Opponent Select")
            inventory.setItem(PLAYER_INDEX, ItemStack(Material.PLAYER_HEAD).apply {
                itemMeta = itemMeta!!.apply {
                    setDisplayName("Another Player")
                }
            })
            inventory.setItem(COMPUTER_INDEX, ItemStack(Material.COMMAND_BLOCK).apply {
                itemMeta = itemMeta!!.apply {
                    setDisplayName("Computer")
                }
            })
            inventory
        }
    }
    object GameTypeSelectInventory {
        const val CHESS_INDEX = 0
        const val CHECKERS_INDEX = 1
        val inventory: Inventory by lazy {
            val inventory = Bukkit.createInventory(null, 18, "${ChatColor.AQUA}Game Select")
            inventory.setItem(CHESS_INDEX, ItemStack(Material.LECTERN).apply {
                itemMeta = itemMeta!!.apply {
                    setDisplayName("Chess")
                }
            })
            inventory.setItem(CHECKERS_INDEX, ItemStack(Material.RED_WOOL).apply {
                itemMeta = itemMeta!!.apply {
                    setDisplayName("Checkers")
                }
            })
            inventory
        }
    }
}