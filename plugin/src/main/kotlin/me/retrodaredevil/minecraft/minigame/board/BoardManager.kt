package me.retrodaredevil.minecraft.minigame.board

import me.retrodaredevil.board.Position
import me.retrodaredevil.minecraft.minigame.chess.ChessPlayer
import me.retrodaredevil.minecraft.minigame.chess.MinecraftChessGame
import org.bukkit.block.Block

class BoardManager(
        val boards: List<WorldBoard>,
) {
    private val gamesRaw = mutableListOf<MinecraftBoardGame>()

    val games: List<MinecraftBoardGame>
        get() {
            gamesRaw.removeIf { it.isOver }
            return gamesRaw
        }


    fun getPosition(block: Block): Pair<WorldBoard, Position>? {
        return boards.firstNotNullOfOrNull {
            val position = it.getPosition(block)
            if (position == null) null else Pair(it, position)
        }
    }
    fun getGame(worldBoard: WorldBoard): MinecraftBoardGame? {
        return games.firstOrNull { it.worldBoard == worldBoard }
    }
    fun startGame(game: MinecraftBoardGame) {
        check(getGame(game.worldBoard) == null) { "This board already has a game on it!" }
        game.startGame()
        gamesRaw.add(game)
    }
}