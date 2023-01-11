package d12

import DaySolver
import java.util.*

class Day12 : DaySolver(12, "Hill Climbing Algorithm") {
    override val exampleInput = """
        Sabqponm
        abcryxxl
        accszExk
        acctuvwj
        abdefghi
    """.trimIndent()

    private val aggregatedData = run {
        val lines = input.lines()
        var s: V? = null
        var e: V? = null
        for ((i, l) in lines.withIndex()) {
            if ("S" in l)
                s = V(l.indexOf("S"), i)
            if ("E" in l)
                e = V(l.indexOf("E"), i)
        }
        val ls = lines.map {
            it.map {
                when (it) {
                    'S' -> 0
                    'E' -> 'z' - 'a'
                    else -> it - 'a'
                }
            }
        }
        R(ls, s!!, e!!)
    }

    override fun firstPart(): String {
        val g = Graph(aggregatedData.lines, aggregatedData.start, aggregatedData.end)

        return g.dijkstra().toString()
    }

    override fun secondPart(): String {
        val g = Graph(aggregatedData.lines, aggregatedData.start, aggregatedData.end)

        return g.dijkstra2().toString()
    }
}

data class R(val lines: List<List<Int>>, val start: V, val end: V)

class Graph(val g: List<List<Int>>, val departure: V, val arrival: V) {
    val width = g[0].size
    val height = g.size
    val uIndices = 0 until width * height

    fun dijkstra(): Int {
        val a = arrival.toInt()
        val visited = mutableSetOf<Int>()
        val distance = mutableMapOf<Int, Int>()
        val queue = PriorityQueue<P>()
        queue.add(P(departure.toInt(), 0))

        while (queue.isNotEmpty()) {
            val (x, p) = queue.poll()
            if (x in visited) continue
            visited += x
            val d = minOf(distance.getOrDefault(x, Int.MAX_VALUE), p)
            distance[x] = d
            if (x == a)
                break

            val neighbors = neighbors(x).filterNot { it in visited }.map { P(it, d + 1) }
            queue.addAll(neighbors)
        }
        return distance[a]!!
    }

    fun dijkstra2(): Int {
        val a = arrival.toInt()
        val visited = mutableSetOf<Int>()
        val distance = mutableMapOf<Int, Int>()
        val queue = PriorityQueue<P>()
        queue.addAll(g.flatMapIndexed { i, it -> it.withIndex().filter { (_, it) -> it == 0 }.map { (j, _) -> P(V(j, i).toInt(), 0) } })

        while (queue.isNotEmpty()) {
            val (x, p) = queue.poll()
            if (x in visited) continue
            visited += x
            val d = minOf(distance.getOrDefault(x, Int.MAX_VALUE), p)
            distance[x] = d
            if (x == a)
                break

            val neighbors = neighbors(x).filterNot { it in visited }.map { P(it, d + 1) }
            queue.addAll(neighbors)
        }
        return distance[a]!!
    }

    fun neighbors(u: Int): List<Int> {
        val v = rCoord(u)

        return neighbors.map { it + v }.filter {
            val x = it.toInt()
            x in uIndices && get(u) + 1 >= get(x)
        }.map { it.toInt() }

    }

    operator fun get(u: Int): Int {
        val r = rCoord(u)
        return g[r.y][r.x]
    }

    fun coord(x: Int, y: Int): Int {
        return y * width + x
    }

    fun rCoord(u: Int): V {
        val y = u / width
        return V(u - y * width, y)
    }

    fun V.toInt(): Int {
        return coord(x, y)
    }

    val neighbors = listOf(V(1, 0), V(-1, 0), V(0, 1), V(0, -1))
}

data class V(val x: Int, val y: Int) {
    operator fun plus(v: V): V {
        return V(x + v.x, y + v.y)
    }
}

data class P(val u: Int, val d: Int) : Comparable<P> {
    override fun compareTo(other: P): Int {
        return d.compareTo(other.d)
    }
}
