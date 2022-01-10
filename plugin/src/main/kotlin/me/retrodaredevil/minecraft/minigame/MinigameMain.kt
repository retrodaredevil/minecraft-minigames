package me.retrodaredevil.minecraft.minigame

import me.retrodaredevil.minecraft.minigame.board.listeners.BoardSelectListener
import me.retrodaredevil.minecraft.minigame.board.scheduled.BoardHighlightTask
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable

class MinigameMain : JavaPlugin() {
    override fun onEnable() {
        super.onEnable()
        logger.info("Enabling minigames plugin!")
        val boardManager = createBoardManager()
        server.pluginManager.registerEvents(BoardSelectListener(boardManager), this)
        server.pluginManager.registerEvents(SimpleListener(), this)
        Bukkit.getScheduler().runTaskTimer(this, BoardHighlightTask(boardManager), 20L, 10L)
    }

    override fun onDisable() {
        super.onDisable()
        logger.info("Disabling minigames plugin!")
    }
}