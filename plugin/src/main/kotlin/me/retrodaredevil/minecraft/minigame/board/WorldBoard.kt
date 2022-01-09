package me.retrodaredevil.minecraft.minigame.board

import me.retrodaredevil.board.BoardPiece
import me.retrodaredevil.board.chess.Position
import org.bukkit.block.Block

interface WorldBoard {

    fun getPosition(block: Block): Position?

    fun <T : BoardPiece> setPiece(placer: PiecePlacer<T>, piece: T?, position: Position)
}