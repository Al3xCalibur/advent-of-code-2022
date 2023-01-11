package d15

import DaySolver
import kotlin.math.abs

class Day15 : DaySolver(15, "Beacon Exclusion Zone") {
    override val exampleInput = """
        Sensor at x=2, y=18: closest beacon is at x=-2, y=15
        Sensor at x=9, y=16: closest beacon is at x=10, y=16
        Sensor at x=13, y=2: closest beacon is at x=15, y=3
        Sensor at x=12, y=14: closest beacon is at x=10, y=16
        Sensor at x=10, y=20: closest beacon is at x=10, y=16
        Sensor at x=14, y=17: closest beacon is at x=10, y=16
        Sensor at x=8, y=7: closest beacon is at x=2, y=10
        Sensor at x=2, y=0: closest beacon is at x=2, y=10
        Sensor at x=0, y=11: closest beacon is at x=2, y=10
        Sensor at x=20, y=14: closest beacon is at x=25, y=17
        Sensor at x=17, y=20: closest beacon is at x=21, y=22
        Sensor at x=16, y=7: closest beacon is at x=15, y=3
        Sensor at x=14, y=3: closest beacon is at x=15, y=3
        Sensor at x=20, y=1: closest beacon is at x=15, y=3
    """.trimIndent()

    private val aggregatedData = parse(input)

    override fun firstPart(): String {
        val limitX = aggregatedData.map { it.coverX }.reduce { acc, x -> acc.max(x) }
        val y = 2_000_000

        val r = limitX.count { x -> aggregatedData.any { it.intersect(Vector(x, y)) } }
        return r.toString()
    }

    override fun secondPart(): String {
        val v = aggregatedData.flatMap { it.outerLimit() }.first { v -> aggregatedData.all { !it.intersect(v) } }
        return (v.x.toLong() * 4000000 + v.y).toString()
    }
}


data class Vector(val x: Int, val y: Int) {
    fun manhattan(o: Vector): Int {
        return abs(x - o.x) + abs(y - o.y)
    }

    operator fun times(l: Int): Vector = Vector(x * l, y * l)
    operator fun plus(u: Vector): Vector = Vector(x + u.x, y + u.y)
}

val downRight = Vector(1, -1)
val downLeft = Vector(-1, -1)
val upLeft = Vector(-1, 1)
val upRight = Vector(1, 1)

val limit = 0..4_000_000

operator fun IntRange.contains(v: Vector): Boolean {
    return v.x in this && v.y in this
}

data class Sensor(val u: Vector, val beacon: Vector) {
    val d = u.manhattan(beacon)
    val coverX = u.x - d..u.x + d
    val coverY = u.y - d..u.y + d

    fun intersect(v: Vector): Boolean {
        return u.manhattan(v) <= d && v != beacon && v != u
    }

    fun outerLimit(): Sequence<Vector> = sequence {
        yieldAll((0..d).asSequence().map { u + Vector(0, d + 1) + downRight * it }.filter { it in limit })
        yieldAll((0..d).asSequence().map { u + Vector(d + 1, 0) + downLeft * it }.filter { it in limit })
        yieldAll((0..d).asSequence().map { u + Vector(0, -d - 1) + upLeft * it }.filter { it in limit })
        yieldAll((0..d).asSequence().map { u + Vector(-d - 1, 0) + upRight * it }.filter { it in limit })
    }
}

fun IntRange.max(x: IntRange): IntRange {
    return minOf(this.first, x.first)..maxOf(this.last, x.last)
}

val lineRegex = Regex("Sensor at x=(-?\\d+), y=(-?\\d+): closest beacon is at x=(-?\\d+), y=(-?\\d+)")

fun parse(s: String): List<Sensor> {
    return s.lineSequence().map {
        val (_, xs, ys, xb, yb) = lineRegex.matchEntire(it)!!.groupValues
        Sensor(Vector(xs.toInt(), ys.toInt()), Vector(xb.toInt(), yb.toInt()))
    }.toList()
}
