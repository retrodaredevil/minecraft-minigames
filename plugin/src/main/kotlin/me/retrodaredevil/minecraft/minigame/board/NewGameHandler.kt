package me.retrodaredevil.minecraft.minigame.board

import me.retrodaredevil.board.checkers.CheckersColor
import me.retrodaredevil.board.chess.ChessColor
import me.retrodaredevil.minecraft.minigame.board.listeners.GameSelectListener
import me.retrodaredevil.minecraft.minigame.checkers.AiCheckersPlayer
import me.retrodaredevil.minecraft.minigame.checkers.BukkitCheckersPlayer
import me.retrodaredevil.minecraft.minigame.checkers.MinecraftCheckersGame
import me.retrodaredevil.minecraft.minigame.chess.AiChessPlayer
import me.retrodaredevil.minecraft.minigame.chess.BukkitChessPlayer
import me.retrodaredevil.minecraft.minigame.chess.MinecraftChessGame
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.time.Duration
import java.util.*

class NewGameHandler(
        private val boardManager: BoardManager,
) {
    private val gamesBeingConfigured = mutableListOf<GameConfig>()

    /**
     * Call this method periodically to make sure that old configs are removed
     */
    fun checkExpiredConfigs() {
        val nanoTime = System.nanoTime()
        val it = gamesBeingConfigured.iterator()
        while (it.hasNext()) {
            val gameConfig = it.next()
            if (gameConfig.expireTimeNanos < nanoTime) {
                it.remove()
                val mainPlayer = Bukkit.getPlayer(gameConfig.mainPlayerId)
                mainPlayer?.sendMessage("Your configuration has expired.")
            }
        }
    }

    private inline fun editConfiguration(player: Player, editFunction: (GameConfig) -> Unit) {
        val gameBeingConfiguredByPlayer = gamesBeingConfigured.firstOrNull { it.mainPlayerId == player.uniqueId }
        if (gameBeingConfiguredByPlayer == null) {
            player.sendMessage("You are not editing a config now! Did it expire?")
        } else {
            editFunction(gameBeingConfiguredByPlayer)
        }
    }

    fun playerSelectOpponent(player: Player, opponentType: OpponentType) {
        editConfiguration(player) {
            it.opponentType = opponentType
            player.sendMessage("Set opponent type to $opponentType")
        }
    }
    fun playerSelectGame(player: Player, gameType: GameType) {
        editConfiguration(player) {
            it.gameType = gameType
            player.sendMessage("Set game type to $gameType")
            if (it.opponentType == OpponentType.PLAYER) {
                player.sendMessage("Now waiting for another player to join.")
                // TODO set the worldBoard to the given game so other players can see what game is about to be played
            } else if (it.opponentType == OpponentType.COMPUTER) {
                startGameAgainstComputer(it.worldBoard, player, gameType, it)
            }
        }
    }

    fun onPlayerRequestBoard(worldBoard: WorldBoard, player: Player) {
        val configForBoard = gamesBeingConfigured.firstOrNull { it.worldBoard == worldBoard }
        if (configForBoard != null && configForBoard.mainPlayerId != player.uniqueId) {
            val gameType = configForBoard.gameType
            if (configForBoard.opponentType == OpponentType.PLAYER && gameType != null) {
                startGameAgainstPlayer(configForBoard.worldBoard, configForBoard.mainPlayerId, player, gameType, configForBoard)
            } else {
                player.sendMessage("Someone else is configuring that board! Please wait until they are done configuring.")
            }
            return
        }
        val gameBeingConfiguredByPlayer = gamesBeingConfigured.firstOrNull { it.mainPlayerId == player.uniqueId }
        if (gameBeingConfiguredByPlayer != null && gameBeingConfiguredByPlayer.worldBoard != worldBoard) {
            gamesBeingConfigured.remove(gameBeingConfiguredByPlayer)
            player.sendMessage("We cancelled your configuration for another board so you could configure this one.")
        }
        if (configForBoard == null) {
            val config = GameConfig(
                    worldBoard, player.uniqueId, null, null,
                    System.nanoTime() + Duration.ofMinutes(2).toNanos()
            )
            gamesBeingConfigured.add(config)
        }
        // If the player is already configuring the board, then just reopen the inventory for them to change stuff
        // If this is a new config, also open the inventory
        player.openInventory(GameSelectListener.OpponentSelectInventory.inventory)
    }

    fun startGameAgainstComputer(worldBoard: WorldBoard, player: Player, gameType: GameType, gameConfigToRemove: GameConfig) {
        gamesBeingConfigured.remove(gameConfigToRemove)
        when (gameType) {
            GameType.CHESS -> {
                boardManager.startGame(MinecraftChessGame(
                        worldBoard,
                        BukkitChessPlayer(ChessColor.WHITE, player),
                        AiChessPlayer(ChessColor.BLACK),
                ))
                player.sendMessage("Game is starting against the AI. You are white.")
            }
            GameType.CHECKERS -> {
                boardManager.startGame(MinecraftCheckersGame(
                        worldBoard,
                        BukkitCheckersPlayer(CheckersColor.RED, player),
                        AiCheckersPlayer(CheckersColor.WHITE),
                ))
                player.sendMessage("Game is starting against the AI. You are red.")
            }
        }
    }
    private fun startGameAgainstPlayer(worldBoard: WorldBoard, waitingPlayerId: UUID, arrivingPlayer: Player, gameType: GameType, gameConfigToRemove: GameConfig) {
        val waitingPlayer = Bukkit.getPlayer(waitingPlayerId)
        if (waitingPlayer == null) {
            arrivingPlayer.sendMessage("The player that configured this board is not online at the moment. Please wait until they rejoin or the configuration expires.")
            return
        }
        gamesBeingConfigured.remove(gameConfigToRemove)
        when (gameType) {
            GameType.CHESS -> {
                boardManager.startGame(MinecraftChessGame(
                        worldBoard,
                        BukkitChessPlayer(ChessColor.WHITE, waitingPlayer),
                        BukkitChessPlayer(ChessColor.BLACK, arrivingPlayer),
                ))
                waitingPlayer.sendMessage("Game is starting with ${arrivingPlayer.name}. You are white.")
                arrivingPlayer.sendMessage("Game is starting with ${waitingPlayer.name}. You are black.")
            }
            GameType.CHECKERS -> {
                boardManager.startGame(MinecraftCheckersGame(
                        worldBoard,
                        BukkitCheckersPlayer(CheckersColor.RED, waitingPlayer),
                        BukkitCheckersPlayer(CheckersColor.WHITE, arrivingPlayer),
                ))
                waitingPlayer.sendMessage("Game is starting with ${arrivingPlayer.name}. You are red.")
                arrivingPlayer.sendMessage("Game is starting with ${waitingPlayer.name}. You are white.")
            }
        }
    }

    class GameConfig(
            val worldBoard: WorldBoard,
            val mainPlayerId: UUID,
            var opponentType: OpponentType?,
            var gameType: GameType?,
            val expireTimeNanos: Long,
    )
    enum class OpponentType {
        PLAYER,
        COMPUTER
    }
    enum class GameType {
        CHESS, CHECKERS
    }
}