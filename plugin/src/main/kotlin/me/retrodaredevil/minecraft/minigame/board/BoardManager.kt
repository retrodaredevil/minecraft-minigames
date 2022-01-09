package me.retrodaredevil.minecraft.minigame.board

import me.retrodaredevil.board.chess.Position
import me.retrodaredevil.minecraft.minigame.chess.ChessPlayer
import me.retrodaredevil.minecraft.minigame.chess.MinecraftChessGame
import org.bukkit.block.Block

class BoardManager(
        val boards: List<WorldBoard>,
) {
    private val chessGamesRaw = mutableListOf<MinecraftChessGame>()

    private val chessGames: List<MinecraftChessGame>
        get() {
            chessGamesRaw.removeIf { it.isOver }
            return chessGamesRaw
        }


    fun getPosition(block: Block): Pair<WorldBoard, Position>? {
        return boards.firstNotNullOfOrNull {
            val position = it.getPosition(block)
            if (position == null) null else Pair(it, position)
        }
    }
    fun getChessGame(worldBoard: WorldBoard): MinecraftChessGame? {
        return chessGames.firstOrNull { it.worldBoard == worldBoard }
    }
    fun startGame(worldBoard: WorldBoard, playerWhite: ChessPlayer, playerBlack: ChessPlayer): MinecraftChessGame {
        check(getChessGame(worldBoard) == null) { "This board already has a chess game on it!" }
        val game = MinecraftChessGame(worldBoard, playerWhite, playerBlack)
        game.updateBoard()
        game.playerWhite.onTurnStart(game)

        chessGamesRaw.add(game)
        return game
    }
}