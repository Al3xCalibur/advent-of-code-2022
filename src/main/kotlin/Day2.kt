package d2

import DaySolver

class Day2 : DaySolver(2, "Rock Paper Scissors") {

    private val aggregatedData = input.lineSequence()
        .map {
            val (opponent, myself) = it.split(" ")
            Round(opponent[0] - 'A', myself[0] - 'X')
        }
        .toList()

    override fun firstPart(): String {
        return aggregatedData.sumOf { it.win() * 3 + it.myself + 1 }
            .toString()
    }

    override fun secondPart(): String {
        return aggregatedData.sumOf { it.myself * 3 + it.myselfValue() + 1 }
            .toString()
    }

    override val exampleInput: String = """
        A Y
        B X
        C Z
    """.trimIndent()
}


data class Round(val opponent: Int, val myself: Int) {
    fun win(): Int {
        return (myself - opponent + 4) % 3
    }

    fun myselfValue(): Int {
        return (opponent + myself + 2) % 3
    }
}

