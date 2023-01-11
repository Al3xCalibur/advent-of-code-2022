package d14

import DaySolver

class Day14 : DaySolver(14, "Regolith Reservoir") {
    override val exampleInput = """
        498,4 -> 498,6 -> 496,6
        503,4 -> 502,4 -> 502,9 -> 494,9
    """.trimIndent()

    private val aggregatedData = parse(input)

    override fun firstPart(): String {
        val g = G(aggregatedData)
        g.walls()

        var i = 0
        while(g.sand()){
            i++
        }

        return i.toString()
    }

    override fun secondPart(): String {
        val g = G(aggregatedData)
        g.walls2()

        var i = 0
        while(g.sand2()){
            i++
        }

        return i.toString()
    }
}

data class Line(val start: Point, val end: Point) {
    fun getPoints(): List<Point> {
        return if (start.x == end.x) {
            val min = minOf(start.y, end.y)
            val max = maxOf(start.y, end.y)
            (min..max).map { Point(start.x, it) }
        } else {
            val min = minOf(start.x, end.x)
            val max = maxOf(start.x, end.x)
            (min..max).map { Point(it, start.y) }
        }
    }
}
data class Point(val x: Int, val y: Int) {
    operator fun plus(p: Point) = Point(x+p.x, y+p.y)
}

class G(val lines: List<Line>) {
    val walls = mutableSetOf<Point>()
    lateinit var limitsX: IntRange
    lateinit var limitsY: IntRange

    fun walls() {
        for (l in lines) {
            walls.addAll(l.getPoints())
        }
        val lim = (walls + Point(500, 0))
        limitsX = lim.minOf { it.x }..lim.maxOf { it.x }
        limitsY = lim.minOf { it.y }..lim.maxOf { it.y }
    }

    fun walls2() {
        for (l in lines) {
            walls.addAll(l.getPoints())
        }
        val lim = (walls + Point(500, 0))
        limitsX = lim.minOf { it.x }..lim.maxOf { it.x }
        limitsY = lim.minOf { it.y }..lim.maxOf { it.y }+2
        val s = limitsY.last-limitsY.first
        for (x in limitsX.first-s..limitsX.endInclusive+s) {
            walls.add(Point(x, limitsY.endInclusive))
        }
    }

    fun sand(): Boolean {
        var p = Point(500,0)
        val below = Point(0, 1)
        val diagonalLeft = Point(-1, 1)
        val diagonalRight = Point(1, 1)
        while(p.x in limitsX && p.y in limitsY) {
            p += if (p + below !in walls) {
                below
            } else {
                if (p+diagonalLeft !in walls) {
                    diagonalLeft
                } else {
                    if (p+diagonalRight !in walls) {
                        diagonalRight
                    } else {
                        walls.add(p)
                        return true
                    }
                }
            }
        }
        return false
    }

    fun sand2(): Boolean {
        var p = Point(500,0)
        val below = Point(0, 1)
        val diagonalLeft = Point(-1, 1)
        val diagonalRight = Point(1, 1)
        while(Point(500, 0) !in walls) {
            p += if (p + below !in walls) {
                below
            } else {
                if (p+diagonalLeft !in walls) {
                    diagonalLeft
                } else {
                    if (p+diagonalRight !in walls) {
                        diagonalRight
                    } else {
                        walls.add(p)
                        return true
                    }
                }
            }
        }
        return false
    }
}

fun parse(s: String): List<Line> {
    return s.lineSequence().flatMap {
        val points = it.split(" -> ").map {
            val (x, y) = it.split(",")
            Point(x.toInt(), y.toInt())
        }
        points.windowed(2, 1).map {
            val (p1, p2) = it
            Line(p1, p2)
        }
    }.toList()
}