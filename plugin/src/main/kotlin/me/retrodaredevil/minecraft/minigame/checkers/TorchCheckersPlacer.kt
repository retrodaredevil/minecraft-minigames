package me.retrodaredevil.minecraft.minigame.checkers

import me.retrodaredevil.board.checkers.CheckersColor
import me.retrodaredevil.board.checkers.CheckersPiece
import me.retrodaredevil.minecraft.minigame.BlockCoordinate
import me.retrodaredevil.minecraft.minigame.board.ClearPiecePlacer
import me.retrodaredevil.minecraft.minigame.board.FlatDirection
import me.retrodaredevil.minecraft.minigame.board.PiecePlacer
import me.retrodaredevil.minecraft.minigame.board.util.getCenter
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.block.BlockFace
import org.bukkit.block.data.Directional
import kotlin.math.absoluteValue

class TorchCheckersPlacer(
        private val redMaterial: Material,
        private val whiteMaterial: Material,
        private val redTorch: Material,
        private val whiteTorch: Material,
        private val redWallTorch: Material,
        private val whiteWallTorch: Material,
) : PiecePlacer<CheckersPiece> {
    override fun place(piece: CheckersPiece?, world: World, lowerLeftCorner: BlockCoordinate, forwardDirection: FlatDirection, tileWidth: Int) {


        ClearPiecePlacer.place(null, world, lowerLeftCorner, forwardDirection, tileWidth)

        val center = getCenter(lowerLeftCorner, forwardDirection, tileWidth)

        if (piece != null) {
            val material: Material
            val torchMaterial: Material
            val wallTorchMaterial: Material
            if (piece.color == CheckersColor.RED) {
                material = redMaterial
                torchMaterial = redTorch
                wallTorchMaterial = redWallTorch
            } else {
                material = whiteMaterial
                torchMaterial = whiteTorch
                wallTorchMaterial = whiteWallTorch
            }
            world.getBlockAt(center.x, center.y + 1, center.z).type = material

            if (piece.isKing) {
                world.getBlockAt(center.x, center.y + 2, center.z).type = torchMaterial
                if (forwardDirection.x.absoluteValue > 0) {
                    // offset on z axis
                    // increasing Z is south
                    // decreasing Z is north
                    world.getBlockAt(center.x, center.y + 1, center.z - 1).apply {
                        type = wallTorchMaterial
                        state.apply {
                            blockData = (blockData as Directional).apply {
                                facing = BlockFace.NORTH
                            }
                            update()
                        }
                    }
                    world.getBlockAt(center.x, center.y + 1, center.z + 1).apply {
                        type = wallTorchMaterial
                        state.apply {
                            blockData = (blockData as Directional).apply {
                                facing = BlockFace.SOUTH
                            }
                            update()
                        }
                    }
                } else {
                    // offset on x axis
                    // increasing X is east
                    // decreasing X is west
                    world.getBlockAt(center.x - 1, center.y + 1, center.z).apply {
                        type = wallTorchMaterial
                        state.apply {
                            blockData = (blockData as Directional).apply {
                                facing = BlockFace.WEST
                            }
                            update()
                        }
                    }
                    world.getBlockAt(center.x + 1, center.y + 1, center.z).apply {
                        type = wallTorchMaterial
                        state.apply {
                            blockData = (blockData as Directional).apply {
                                facing = BlockFace.EAST
                            }
                            update()
                        }
                    }
                }
            }
        }
    }
    companion object {
        fun createDefault(blackInsteadOfWhite: Boolean = false): TorchCheckersPlacer {
            if (blackInsteadOfWhite) {
                return TorchCheckersPlacer(
                        Material.RED_NETHER_BRICKS, Material.DEEPSLATE_TILES,
                        Material.TORCH, Material.SOUL_TORCH,
                        Material.WALL_TORCH, Material.SOUL_WALL_TORCH
                )
            }

            return TorchCheckersPlacer(
                    Material.RED_NETHER_BRICKS, Material.QUARTZ_BLOCK,
                    Material.TORCH, Material.REDSTONE_TORCH,
                    Material.WALL_TORCH, Material.REDSTONE_WALL_TORCH
            )

        }
    }
}