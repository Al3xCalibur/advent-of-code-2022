package d9

import DaySolver
import kotlin.math.abs

class Day9 : DaySolver(9, "Treetop Tree House") {
    override val exampleInput: String = """
        R 4
        U 4
        L 3
        D 1
        R 4
        D 1
        L 5
        R 2
    """.trimIndent()

    private val aggregatedData = input.lineSequence().map {
        val (dir, n) = it.split(" ")
        MoveM(Direction.fromString(dir), n.toInt())
    }.toList()

    override fun firstPart(): String {
        val s = System(false)
        aggregatedData.forEach { s.move(it) }
        return s.visitedPlaces.size.toString()
    }

    override fun secondPart(): String {
        val s = System(true)
        aggregatedData.forEach { s.move(it) }
        return s.visitedPlaces.size.toString()
    }
}


class System(p2: Boolean) {
    val size = if (p2) 9 else 1
    val visitedPlaces = HashSet<Vector>()
    var elements = MutableList(size+1) { Vector(0, 0) }

    init {
        visitedPlaces.add(elements.last())
    }

    fun move(m: MoveM) {
        repeat(m.n) {
            unitMove(direction = m.direction)
        }
    }

    fun unitMove(direction: Direction) {
        elements[0] += dirToVector(direction)

        for(i in 1..elements.lastIndex) {
            elements[i] += elements[i].move(elements[i-1])
        }

        visitedPlaces.add(elements.last())
    }


    fun dirToVector(direction: Direction): Vector {
        return when (direction) {
            Direction.UP -> Vector(0, -1)
            Direction.RIGHT -> Vector(1, 0)
            Direction.DOWN -> Vector(0, 1)
            Direction.LEFT -> Vector(-1, 0)
        }
    }
}

data class Vector(val x: Int, val y: Int) {
    operator fun plus(v: Vector) = Vector(x + v.x, y + v.y)
    operator fun minus(v: Vector) = Vector(x - v.x, y - v.y)


    fun move(head: Vector): Vector {
        val diff = head - this
        if (abs(diff.x) <= 1 && abs(diff.y) <= 1) return Vector(0, 0)
        return Vector(sign(diff.x), sign(diff.y))
    }
}

fun sign(i: Int): Int {
    return if (i < 0) -1 else if (i == 0) 0 else 1
}

data class MoveM(val direction: Direction, val n: Int)

enum class Direction {
    UP, RIGHT, DOWN, LEFT;

    companion object {
        val values = Direction.values()
        fun fromString(str: String): Direction {
            return values.first { it.toString().startsWith(str) }
        }
    }
}

