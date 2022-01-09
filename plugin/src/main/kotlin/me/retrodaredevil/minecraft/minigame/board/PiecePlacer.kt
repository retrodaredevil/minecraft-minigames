package me.retrodaredevil.minecraft.minigame.board

import me.retrodaredevil.board.BoardPiece
import me.retrodaredevil.minecraft.minigame.BlockCoordinate
import org.bukkit.World

interface PiecePlacer<T : BoardPiece> {
    fun place(piece: T?, world: World, lowerLeftCorner: BlockCoordinate, forwardDirection: FlatDirection, tileWidth: Int)
}