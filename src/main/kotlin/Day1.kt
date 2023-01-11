package d1

import DaySolver

class Day1 : DaySolver(1, "Calorie Counting") {
    private val aggregatedData = input.splitToSequence("\n\n")
        .map {
            it.lineSequence().sumOf { calorie -> calorie.toLong() }
        }
        .toList()

    override fun firstPart(): String {
        return aggregatedData.max()
            .toString()
    }

    override fun secondPart(): String {
        return aggregatedData.sortedDescending().take(3).sum()
            .toString()
    }

    override val exampleInput: String = """
        1000
        2000
        3000

        4000

        5000
        6000

        7000
        8000
        9000

        10000
    """.trimIndent()
}
