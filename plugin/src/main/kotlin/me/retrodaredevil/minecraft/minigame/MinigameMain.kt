package me.retrodaredevil.minecraft.minigame

import me.retrodaredevil.minecraft.minigame.board.listeners.BoardSelectListener
import org.bukkit.plugin.java.JavaPlugin

class MinigameMain : JavaPlugin() {
    override fun onEnable() {
        super.onEnable()
        logger.info("Enabling minigames plugin!")
        server.pluginManager.registerEvents(BoardSelectListener(createBoardManager()), this)

    }

    override fun onDisable() {
        super.onDisable()
        logger.info("Disabling minigames plugin!")
    }
}