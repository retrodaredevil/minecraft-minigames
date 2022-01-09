package me.retrodaredevil.minecraft.minigame.chess

import me.retrodaredevil.board.chess.*
import me.retrodaredevil.minecraft.minigame.board.MinecraftBoardGame
import me.retrodaredevil.minecraft.minigame.board.WorldBoard
import org.bukkit.Material

class MinecraftChessGame(
        override val worldBoard: WorldBoard,
        val playerWhite: ChessPlayer,
        val playerBlack: ChessPlayer,
) : MinecraftBoardGame {
    init {
        require(playerWhite.color == ChessColor.WHITE)
        require(playerBlack.color == ChessColor.BLACK)
    }
    val players = listOf(playerWhite, playerBlack)
    val game = ChessGame()
    private val placer = ChessPlacer(Material.WHITE_WOOL, Material.BLACK_WOOL)
    private var done = false

    fun updateBoard() {
        for (row in 0..7) {
            for (column in 0..7) {
                worldBoard.setPiece(placer, null, Position(column, row))
            }
        }
        for (pieceData in game.state.activePieces) {
            worldBoard.setPiece(placer, pieceData.piece, pieceData.position)
        }
    }

    fun move(move: ChessMove) {
        // TODO pawn promotion stuff
        game.move(move, if (move.moveType == ChessMove.Type.PAWN_PROMOTION) PieceType.QUEEN else null)
        println(game.state.getKingsInCheck())

        updateBoard()
        val result = game.getResult()
        if (result != null) {
            done = true
            players.forEach { it.onGameEnd(result) }
        } else {
            // This must be at the end because move may be called recursively depending on the implementation of the player
            players.firstOrNull { it.color == game.turn }!!.onTurnStart(this)
        }
    }

    override val isOver: Boolean
        get() = game.getResult() != null
}