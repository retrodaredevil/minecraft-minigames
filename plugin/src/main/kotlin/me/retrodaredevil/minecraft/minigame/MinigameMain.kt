package me.retrodaredevil.minecraft.minigame

import me.retrodaredevil.minecraft.minigame.board.NewGameHandler
import me.retrodaredevil.minecraft.minigame.board.listeners.BoardSelectListener
import me.retrodaredevil.minecraft.minigame.board.listeners.GameSelectListener
import me.retrodaredevil.minecraft.minigame.board.scheduled.BoardHighlightTask
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable

class MinigameMain : JavaPlugin() {
    override fun onEnable() {
        super.onEnable()
        logger.info("Enabling minigames plugin!")
        val boardManager = createBoardManager()
        val newGameHandler = NewGameHandler(boardManager)
        server.pluginManager.registerEvents(BoardSelectListener(boardManager, newGameHandler), this)
        server.pluginManager.registerEvents(SimpleListener(), this)
        server.pluginManager.registerEvents(GameSelectListener(newGameHandler), this)
        Bukkit.getScheduler().runTaskTimer(this, BoardHighlightTask(boardManager), 0L, 10L)
        Bukkit.getScheduler().runTaskTimer(this, newGameHandler::checkExpiredConfigs, 2L, 20L)
    }

    override fun onDisable() {
        super.onDisable()
        logger.info("Disabling minigames plugin!")
    }
}