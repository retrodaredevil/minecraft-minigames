package me.retrodaredevil.board.othello

import me.retrodaredevil.board.Position

class OthelloData {
    private val data = arrayOfNulls<OthelloColor>(64)

    constructor() {

    }
    constructor(source: OthelloData) {
        System.arraycopy(source.data, 0, data, 0, 64)
    }

    private fun getIndex(position: Position): Int {
        return position.columnIndex + position.rowIndex * 8
    }

    fun getTile(position: Position): OthelloColor? {
        return data[getIndex(position)]
    }
    fun setTile(position: Position, color: OthelloColor?) {
        data[getIndex(position)] = color
    }
    fun flipTile(position: Position) {
        val color = getTile(position) ?: error("position: $position does not have a tile! Cannot flip it!")
        setTile(position, color.opposite)
    }
    fun copy(): OthelloData {
        return OthelloData(this)
    }
}

class OthelloState(
        private val data: OthelloData
) {

    fun getTile(position: Position): OthelloColor? {
        return data.getTile(position)
    }

    fun getPossibleMoves(color: OthelloColor): List<OthelloMove> {
        val moves = mutableListOf<OthelloMove>()
        for (position in Position.ALL) {
            val allFlipPositions = mutableListOf<Position>()
            for ((directionColumn, directionRow) in arrayOf(Pair(1, 0), Pair(0, 1), Pair(-1, 0), Pair(0, -1), Pair(1, 1), Pair(-1, 1), Pair(1, -1), Pair(-1, -1))) {
                val flipPositions = getFlippedFromPosition(position, color, directionColumn, directionRow)
                allFlipPositions.addAll(flipPositions)
            }
            if (allFlipPositions.isNotEmpty()) {
                moves.add(OthelloMove(color, position, allFlipPositions))
            }
        }
        return moves
    }

    private fun getFlippedFromPosition(position: Position, color: OthelloColor, directionColumn: Int, directionRow: Int): List<Position> {
        require(directionColumn != 0 || directionRow != 0)

        if (data.getTile(position) != null) {
            return emptyList()
        }
        val positions = mutableListOf<Position>()
        var columnIndex = position.columnIndex
        var rowIndex = position.rowIndex
        while (true) {
            columnIndex += directionColumn
            rowIndex += directionRow
            val currentPosition = Position.ofOrNull(columnIndex, rowIndex) ?: return emptyList()

            val currentColor = data.getTile(currentPosition) ?: return emptyList()
            if (currentColor == color) {
                return positions
            }
            positions.add(currentPosition)
        }
    }

    fun move(move: OthelloMove): OthelloState {
        val data = data.copy()
        data.setTile(move.position, move.color)
        for (position in move.flipPositions) {
            data.flipTile(position)
        }
        return OthelloState(data)
    }
    fun getTileCount(): TileCount {
        var white = 0
        var black = 0
        for (position in Position.ALL) {
            when (data.getTile(position)) {
                OthelloColor.WHITE -> white++
                OthelloColor.BLACK -> black++
                null -> {}
            }
        }
        return TileCount(white, black)
    }

    companion object {
        fun createDefault(): OthelloState {
            val data = OthelloData()
            data.setTile(Position(3, 3), OthelloColor.BLACK)
            data.setTile(Position(4, 4), OthelloColor.BLACK)

            data.setTile(Position(3, 4), OthelloColor.WHITE)
            data.setTile(Position(4, 3), OthelloColor.WHITE)
            return OthelloState(data)
        }
    }
    data class TileCount(
            val whiteCount: Int,
            val blackCount: Int,
    ) {
        val advantage: OthelloColor?
            get() = when {
                whiteCount == blackCount -> null
                whiteCount > blackCount -> OthelloColor.WHITE
                else -> OthelloColor.BLACK
            }
    }
}