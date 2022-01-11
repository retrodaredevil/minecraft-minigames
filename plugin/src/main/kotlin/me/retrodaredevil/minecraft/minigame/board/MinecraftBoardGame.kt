package me.retrodaredevil.minecraft.minigame.board

interface MinecraftBoardGame {
    val worldBoard: WorldBoard
    val isOver: Boolean

    val players: List<BoardGamePlayer>

    fun startGame()
}