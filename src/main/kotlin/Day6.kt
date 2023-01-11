package d6

import DaySolver

class Day6 : DaySolver(6, "Tuning Trouble") {
    override val exampleInput: String = """
        mjqjpqmgbljsphdztnvjfqwrcgsmlb
    """.trimIndent()

    private val aggregatedData = input

    override fun firstPart(): String {
        val size = 4
        val index = aggregatedData.windowed(size).indexOfFirst { it.toSet().size == size }

        return (index + size).toString()
    }

    override fun secondPart(): String {
        val size = 14
        val index = aggregatedData.windowed(size).indexOfFirst { it.toSet().size == size }

        return (index + size).toString()
    }
}
