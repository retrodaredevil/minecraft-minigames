package me.retrodaredevil.minecraft.minigame.board

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class BoardGameCommandHandler(
        private val boardActionHandler: BoardActionHandler,
) : CommandExecutor {

    internal fun register(plugin: JavaPlugin) {
        plugin.getCommand("forfeit")!!.setExecutor(this)
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (label.equals("forfeit", ignoreCase = true) || label.equals("ff", ignoreCase = true)) {
            if (sender !is Player) {
                sender.sendMessage("Only players can use this!")
                return true
            }
            startForfeit(sender)
            return true
        }
        return false
    }

    private fun startForfeit(player: Player) {
        player.sendMessage("Click on the board you would like to forfeit on.")
        boardActionHandler.configureAction(player, BoardActionHandler.Action.FORFEIT)
    }
    private fun startDraw(player: Player) {
        player.sendMessage("Click on the board you would like to request a draw on.")
    }
}