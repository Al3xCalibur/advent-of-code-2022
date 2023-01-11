package d4

import DaySolver

class Day4 : DaySolver(4, "Camp Cleanup") {
    override val exampleInput: String = """
        2-4,6-8
        2-3,4-5
        5-7,7-9
        2-8,3-7
        6-6,4-6
        2-6,4-8
    """.trimIndent()

    private val aggregatedData = input.lineSequence().map {
        val (first, second) = it.split(",")
        val fR = first.toIntRange()
        val sR = second.toIntRange()
        fR to sR
    }

    override fun firstPart(): String {
        return aggregatedData.count {
            it.first.isIncluded(it.second) || it.second.isIncluded(it.first)
        }
            .toString()
    }

    override fun secondPart(): String {
        return aggregatedData.count{ it.first.isOverlapping(it.second) }
            .toString()
    }
}

fun String.toIntRange() : IntRange {
    val (f, s) = this.split("-")
    return IntRange(f.toInt(), s.toInt())
}

fun IntRange.isIncluded(other: IntRange): Boolean {
    return this.first <= other.first && other.last <= this.last
}

fun IntRange.isOverlapping(other: IntRange): Boolean {
    return other.first in this || other.last in this || this.last in other || this.first in other
}