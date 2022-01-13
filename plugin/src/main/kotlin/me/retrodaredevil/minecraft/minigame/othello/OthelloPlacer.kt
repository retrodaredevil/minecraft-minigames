package me.retrodaredevil.minecraft.minigame.othello

import me.retrodaredevil.board.othello.OthelloColor
import me.retrodaredevil.minecraft.minigame.BlockCoordinate
import me.retrodaredevil.minecraft.minigame.board.ClearPiecePlacer
import me.retrodaredevil.minecraft.minigame.board.FlatDirection
import me.retrodaredevil.minecraft.minigame.board.PiecePlacer
import me.retrodaredevil.minecraft.minigame.board.PiecePlacerSwitch
import me.retrodaredevil.minecraft.minigame.board.util.getCenter
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.data.Bisected
import org.bukkit.block.data.type.Stairs

class OthelloPlacer(
        private val centerMaterial: Material,
        private val stairMaterial: Material
) : PiecePlacer<OthelloColor> {
    override fun place(piece: OthelloColor?, world: World, lowerLeftCorner: BlockCoordinate, forwardDirection: FlatDirection, tileWidth: Int) {
        ClearPiecePlacer.place(null, world, lowerLeftCorner, forwardDirection, tileWidth)
        val center = getCenter(lowerLeftCorner, forwardDirection, tileWidth)

        if (piece != null) {
            world.getBlockAt(center.x, center.y + 1, center.z).type = centerMaterial
            placeStair(world.getBlockAt(center.x, center.y + 1, center.z + 1), BlockFace.NORTH)
            placeStair(world.getBlockAt(center.x, center.y + 1, center.z - 1), BlockFace.SOUTH)
            placeStair(world.getBlockAt(center.x + 1, center.y + 1, center.z), BlockFace.WEST)
            placeStair(world.getBlockAt(center.x - 1, center.y + 1, center.z), BlockFace.EAST)
        }
    }
    private fun placeStair(block: Block, face: BlockFace) {
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

    companion object {
        val WHITE = OthelloPlacer(Material.SMOOTH_QUARTZ, Material.SMOOTH_QUARTZ_STAIRS)
        val BLACK = OthelloPlacer(Material.BLACK_CONCRETE, Material.POLISHED_BLACKSTONE_BRICK_STAIRS)

        fun createDefault(): PiecePlacer<OthelloColor> {
            return PiecePlacerSwitch({ it }, OthelloColor.WHITE, WHITE, OthelloColor.BLACK, BLACK)
        }
    }
}