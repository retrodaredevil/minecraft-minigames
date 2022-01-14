package me.retrodaredevil.minecraft.minigame.othello

import me.retrodaredevil.board.othello.OthelloColor
import me.retrodaredevil.minecraft.minigame.BlockCoordinate
import me.retrodaredevil.minecraft.minigame.board.ClearPiecePlacer
import me.retrodaredevil.minecraft.minigame.board.FlatDirection
import me.retrodaredevil.minecraft.minigame.board.PiecePlacer
import me.retrodaredevil.minecraft.minigame.board.PiecePlacerSwitch
import me.retrodaredevil.minecraft.minigame.board.util.getCenter
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.Skull
import org.bukkit.block.data.Bisected
import org.bukkit.block.data.Directional
import org.bukkit.block.data.type.Stairs
import org.bukkit.inventory.meta.ItemMeta
import java.util.*

class OthelloPlacer(
        private val centerMaterial: Material,
        private val oppositeBlockFace: Boolean,
        private val outerPlacer: (Block, BlockFace) -> Unit,
) : PiecePlacer<OthelloColor> {
    override fun place(piece: OthelloColor?, world: World, lowerLeftCorner: BlockCoordinate, forwardDirection: FlatDirection, tileWidth: Int) {
        ClearPiecePlacer.place(null, world, lowerLeftCorner, forwardDirection, tileWidth)
        val center = getCenter(lowerLeftCorner, forwardDirection, tileWidth)

        if (piece != null) {
            world.getBlockAt(center.x, center.y + 1, center.z).type = centerMaterial
            outerPlacer(world.getBlockAt(center.x, center.y + 1, center.z + 1), BlockFace.NORTH.let { if (oppositeBlockFace) it.oppositeFace else it })
            outerPlacer(world.getBlockAt(center.x, center.y + 1, center.z - 1), BlockFace.SOUTH.let { if (oppositeBlockFace) it.oppositeFace else it })
            outerPlacer(world.getBlockAt(center.x + 1, center.y + 1, center.z), BlockFace.WEST.let { if (oppositeBlockFace) it.oppositeFace else it })
            outerPlacer(world.getBlockAt(center.x - 1, center.y + 1, center.z), BlockFace.EAST.let { if (oppositeBlockFace) it.oppositeFace else it })
        }
    }

    companion object {
        val WHITE_WITH_STAIRS = OthelloPlacer(Material.SMOOTH_QUARTZ, false) { block, face -> placeStair(Material.SMOOTH_QUARTZ_STAIRS, block, face) }
        val BLACK_WITH_STAIRS = OthelloPlacer(Material.BLACK_CONCRETE, false) { block, face -> placeStair(Material.POLISHED_BLACKSTONE_BRICK_STAIRS, block, face) }

//        val WHITE = OthelloPlacer(Material.IRON_BLOCK, true) { block, face -> placeSkull(UUID.fromString("a24b64c6-3365-46ae-9e4c-9453b33cb2f9"), block, face) } // metalhedd
//        val BLACK = OthelloPlacer(Material.OBSIDIAN, true) { block, face -> placeSkull(UUID.fromString("e1f139f7-fa3b-4b66-8584-9839319f877f"), block, face) } // loiwiol
        val WHITE = OthelloPlacer(Material.IRON_BLOCK, true) { block, face -> placeSkull("metalhedd", block, face) }
        val BLACK = OthelloPlacer(Material.OBSIDIAN, true) { block, face -> placeSkull("loiwiol", block, face) }

        fun createDefault(): PiecePlacer<OthelloColor> {
            return PiecePlacerSwitch({ it }, OthelloColor.WHITE, WHITE, OthelloColor.BLACK, BLACK)
        }
        private fun placeStair(stairMaterial: Material, block: Block, face: BlockFace) {
            block.type = stairMaterial
            block.state.apply {
                blockData = (blockData as Stairs).apply {
                    facing = face
                    half = Bisected.Half.BOTTOM
                    shape = Stairs.Shape.STRAIGHT
                }
                update()
            }
        }
        private fun placeSkull(ownerName: String, block: Block, face: BlockFace) {
            block.type = Material.PLAYER_WALL_HEAD
            (block.state as Skull).apply {
                blockData = (blockData as Directional).apply {
                    facing = face
                }
                // This does make the server lag the first time it has to load them, but after that it is fine.
                @Suppress("DEPRECATION") // we cannot use the setOwningPlayer method because that requires the players to have joined our server before
                owner = ownerName

                update()
            }
        }
    }
}