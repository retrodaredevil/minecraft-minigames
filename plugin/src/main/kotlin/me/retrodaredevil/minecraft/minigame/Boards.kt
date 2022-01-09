package me.retrodaredevil.minecraft.minigame

import me.retrodaredevil.minecraft.minigame.board.BoardManager
import me.retrodaredevil.minecraft.minigame.board.FlatDirection
import me.retrodaredevil.minecraft.minigame.board.implementations.SimpleWorldBoard
import me.retrodaredevil.minecraft.minigame.chess.ChessPlacer
import org.bukkit.Bukkit
import org.bukkit.Material


fun createBoardManager(): BoardManager {
    val a1Coordinate = BlockCoordinate(-10, 67, 10)
    val forwardDirection = FlatDirection(0, 1)
    val board = SimpleWorldBoard(Bukkit.getWorld("WorldHub") ?: error("WorldHub does not exist!?!"), a1Coordinate, forwardDirection, 3, 3)

    return BoardManager(listOf(board))
}
