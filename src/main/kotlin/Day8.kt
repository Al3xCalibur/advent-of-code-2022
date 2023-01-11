package d8

import DaySolver

class Day8 : DaySolver(8, "Treetop Tree House") {
    override val exampleInput: String = """
        30373
        25512
        65332
        33549
        35390
    """.trimIndent()

    private val aggregatedData = Grid(input
        .lineSequence()
        .map {
            it.map { it - '0' }
        }.toList())

    override fun firstPart(): String {
        aggregatedData.markVisible()

        return aggregatedData.visible.sumOf { it.count { it } }.toString()
    }

    override fun secondPart(): String {

        return aggregatedData.scenic().toString()
    }
}


class Grid(val trees: List<List<Int>>) {
    val visible = trees.map { it.map { false }.toMutableList() }
    val width = trees[0].size
    val height = trees.size

    fun markVisible() {
        for (i in 0 until height) {
            var max = -1
            for(j in 0 until width) {
                val tree = trees[i][j]
                if (tree > max)
                    visible[i][j] = true
                max = maxOf(max, tree)
            }
        }
        for (i in 0 until height) {
            var max = -1
            for(j in width-1 downTo 0) {
                val tree = trees[i][j]
                if (tree > max)
                    visible[i][j] = true
                max = maxOf(max, tree)
            }
        }
        for (j in 0 until width) {
            var max = -1
            for(i in 0 until height) {
                val tree = trees[i][j]
                if (tree > max)
                    visible[i][j] = true
                max = maxOf(max, tree)
            }
        }
        for (j in 0 until width) {
            var max = -1
            for(i in height-1 downTo 0) {
                val tree = trees[i][j]
                if (tree > max)
                    visible[i][j] = true
                max = maxOf(max, tree)
            }
        }
    }

    fun scenic(): Int {
        return (1 until height-1).maxOf { i ->
            (1 until width - 1).maxOf { j ->
                scenic(i, j)
            }
        }
    }

    fun scenic(i: Int, j: Int): Int {
        val h = trees[i][j]
        var countLeft = 0
        for (left in j-1 downTo 0) {
            val tree = trees[i][left]
            countLeft++
            if (tree >= h) break
        }
        var countRight = 0
        for (right in j+1 until width) {
            val tree = trees[i][right]
            countRight++
            if (tree >= h) break
        }
        var countTop = 0
        for (top in i-1 downTo 0) {
            val tree = trees[top][j]
            countTop++
            if (tree >= h) break
        }
        var countDown = 0
        for (down in i+1 until height) {
            val tree = trees[down][j]
            countDown++
            if (tree >= h) break
        }
        return countLeft * countRight * countDown * countTop
    }
}
