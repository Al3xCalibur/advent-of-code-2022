package d3

import DaySolver

class Day3 : DaySolver(3, "Rucksack Reorganization") {
    override val exampleInput: String = """
        vJrwpWtwJgWrhcsFMMfFFhFp
        jqHRNqRjqzjGDLGLrsFMfFZSrLrFZsSL
        PmmdzqPrVvPwwTWBwg
        wMqvLMZHhHMvwLHjbvcjnnSBnvTQFn
        ttgJtRGJQctTZtZT
        CrZsJsPPZsGzwwsLwLmpwMDw
    """.trimIndent()

    private val aggregatedData = input.lines()

    override fun firstPart(): String {
        return aggregatedData.sumOf {
                val split = it.halves()
                split.findDuplicate().priority()
            }
            .toString()
    }

    override fun secondPart(): String {
        return aggregatedData
            .windowed(3, 3)
            .sumOf { it.findDuplicate().priority() }
            .toString()
    }
}

fun Pair<String, String>.findDuplicate(): Char {
    val m1 = this.first.toSet()
    val m2 = this.second.toSet()
    return m1.intersect(m2).first()
}

fun List<String>.findDuplicate(): Char {
    val intersection = this.map { it.toSet() }.reduce { acc, chars -> acc.intersect(chars) }
    return intersection.first()
}

fun Char.priority(): Int {
    if (this in 'a'..'z')
        return this - 'a' + 1
    return this - 'A' + 27
}

fun String.halves(): Pair<String, String> {
    val h = this.length/2
    return this.substring(0, h) to this.substring(h)
}
