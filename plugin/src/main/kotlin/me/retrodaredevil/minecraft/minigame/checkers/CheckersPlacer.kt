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

class CheckersPlacer(
        private val redMaterial: Material,
        private val whiteMaterial: Material,
        private val redKingMaterial: Material,
        private val whiteKingMaterial: Material,
        private val redKingMaterialTopper: Material,
        private val whiteKingMaterialTopper: Material,
) : PiecePlacer<CheckersPiece> {
    override fun place(piece: CheckersPiece?, world: World, lowerLeftCorner: BlockCoordinate, forwardDirection: FlatDirection, tileWidth: Int) {

        val center = getCenter(lowerLeftCorner, forwardDirection, tileWidth)

        ClearPiecePlacer.place(null, world, lowerLeftCorner, forwardDirection, tileWidth)

        if (piece != null) {
            val material: Material
            val kingMaterial: Material
            val kingMaterialTopper: Material
            if (piece.color == CheckersColor.RED) {
                material = redMaterial
                kingMaterial = redKingMaterial
                kingMaterialTopper = redKingMaterialTopper
            } else {
                material = whiteMaterial
                kingMaterial = whiteKingMaterial
                kingMaterialTopper = whiteKingMaterialTopper
            }
            if (piece.isKing) {
                world.getBlockAt(center.x, center.y + 1, center.z).type = kingMaterial
                world.getBlockAt(center.x, center.y + 2, center.z).type = kingMaterialTopper
                world.getBlockAt(center.x, center.y + 3, center.z).type = Material.AIR
            } else {
                world.getBlockAt(center.x, center.y + 1, center.z).type = material
                world.getBlockAt(center.x, center.y + 2, center.z).type = Material.AIR
                world.getBlockAt(center.x, center.y + 3, center.z).type = Material.AIR
            }
        }
    }
    companion object {
        fun createDefault(): CheckersPlacer {
            return CheckersPlacer(
                    Material.RED_WOOL, Material.WHITE_WOOL,
                    Material.RED_WOOL, Material.WHITE_WOOL,
                    Material.EXPOSED_CUT_COPPER_SLAB, Material.DIORITE_SLAB,
            )
        }
    }
}