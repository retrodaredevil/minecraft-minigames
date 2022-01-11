package me.retrodaredevil.minecraft.minigame.chess

import me.retrodaredevil.board.Position
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
    override val players = listOf(playerWhite, playerBlack)
    val game = ChessGame()
    private val placer = ChessPlacer(Material.WHITE_WOOL, Material.BLACK_WOOL)

    override fun startGame() {
        updateBoard()
        playerWhite.onTurnStart(this)
    }

    private fun updateBoard() {
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
        check(move.piece.color == game.turn) { "It's not your turn!!! move: $move turn: ${game.turn}"}
        // TODO pawn promotion stuff
        println("Turn before move: ${game.turn}")
        game.move(move, if (move.moveType == ChessMove.Type.PAWN_PROMOTION) PieceType.QUEEN else null)
        println("Turn after move: ${game.turn}")
        println("kings in check: ${game.state.getKingsInCheck()}")

        updateBoard()
        val result = game.getResult()
        println("result: $result")
        if (result != null) {
            players.forEach { it.onGameEnd(result) }
        } else {
            // This must be at the end because move may be called recursively depending on the implementation of the player
            getPlayer(game.turn).onTurnStart(this)
        }
    }

    override val isOver: Boolean
        get() = game.getResult() != null

    fun getPlayer(color: ChessColor) = when(color) {
        ChessColor.WHITE -> playerWhite
        ChessColor.BLACK -> playerBlack
    }
}