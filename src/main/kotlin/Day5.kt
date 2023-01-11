package d5

import DaySolver

class Day5 : DaySolver(5, "Supply Stacks") {
    override val exampleInput: String = """
            [D]    
        [N] [C]    
        [Z] [M] [P]
         1   2   3 
        
        move 1 from 2 to 1
        move 3 from 1 to 3
        move 2 from 2 to 1
        move 1 from 1 to 2
    """.trimIndent()

    private val aggregatedData = run {
        val (initialState, moves) = input.split("\n\n")
        val m = moves.lineSequence().map {
            val (_, moveCount, from, to) = movesRegex.matchEntire(it)!!.groupValues
            Move(moveCount.toInt(), from.toInt() - 1, to.toInt() - 1)
        }.toList()
        initialState to m
    }

    override fun firstPart(): String {
        val state = aggregatedData.first.getInitialState()
        val moves = aggregatedData.second

        moves.forEach { move ->
            repeat(move.n) {
                state[move.to].add(state[move.from].removeLast())
            }
        }

        return getResult(state)
    }

    private fun getResult(state: List<MutableList<Char>>) =
        state.joinToString("") { it.last().toString() }

    override fun secondPart(): String {
        val state = aggregatedData.first.getInitialState()
        val moves = aggregatedData.second
        val tempStack = ArrayList<Char>()

        moves.forEach { move ->
            repeat(move.n) {
                tempStack.add(state[move.from].removeLast())
            }
            repeat(move.n) {
                state[move.to].add(tempStack.removeLast())
            }
        }

        return getResult(state)
    }
}

private val movesRegex = Regex("move (\\d+) from (\\d) to (\\d)")

data class Move(val n: Int, val from: Int, val to: Int)

fun String.getInitialState(): List<MutableList<Char>> {
    val lines = this.lines()
    val columnCount = (lines.last().length - 3) / 4 + 1

    val result = List(columnCount) { ArrayList<Char>() }
    for (i in lines.lastIndex - 1 downTo 0) {
        repeat(columnCount) {
            val c = lines[i][it * 4 + 1]
            if (c.isLetter())
                result[it].add(c)
        }
    }
    return result
}
