package d16

import DaySolver
import java.util.LinkedList

class Day16 : DaySolver(16, "Proboscidea Volcanium") {
    override val exampleInput = """
        Valve AA has flow rate=0; tunnels lead to valves DD, II, BB
        Valve BB has flow rate=13; tunnels lead to valves CC, AA
        Valve CC has flow rate=2; tunnels lead to valves DD, BB
        Valve DD has flow rate=20; tunnels lead to valves CC, AA, EE
        Valve EE has flow rate=3; tunnels lead to valves FF, DD
        Valve FF has flow rate=0; tunnels lead to valves EE, GG
        Valve GG has flow rate=0; tunnels lead to valves FF, HH
        Valve HH has flow rate=22; tunnel leads to valve GG
        Valve II has flow rate=0; tunnels lead to valves AA, JJ
        Valve JJ has flow rate=21; tunnel leads to valve II
    """.trimIndent()

    private val graph = parse(input)

    override fun firstPart(): String {
        val s = System(graph, 30)
        val root = graph.keys.first { it.name == "AA" }
        val score = s.recur(0, root, setOf(), 0)
        return s.score.toString()
    }

    override fun secondPart(): String {
        val s = System(graph, 26)
        val root = graph.keys.first { it.name == "AA" }
        s.recur(0, root, setOf(), 0, true)
        return s.score.toString()
    }
}

class System(val shortestPaths: Map<Valve, Map<Valve, Int>>, val totalTime: Int) {
    val root = shortestPaths.keys.first { it.name == "AA" }
    var score = 0
    fun recur(
        currScore: Int,
        currentValve: Valve,
        visited: Set<Valve>,
        time: Int,
        part2: Boolean = false
    ) {
        score = maxOf(score, currScore)
        for ((valve, dist) in shortestPaths[currentValve]!!) {
            if (valve !in visited && time + dist + 1 < totalTime) {
                recur(
                    currScore + (totalTime - time - dist - 1) * valve.rate,
                    valve,
                    visited + valve,
                    time + dist + 1,
                    part2
                )
            }
        }
        if (part2)
            recur(currScore, shortestPaths.keys.first { it.name == "AA" }, visited, 0, false)
    }
}

data class Valve(val name: String, val rate: Int, val tunnels: List<String>)

val lineRegex = Regex("Valve (\\w+) has flow rate=(\\d+); tunnels? leads? to valves? (.+)")

const val inf: Int = Int.MAX_VALUE / 2
fun parse(s: String): Map<Valve, Map<Valve, Int>> {
    val valves = s.lineSequence().map {
        val (_, name, rate, l) = lineRegex.matchEntire(it)!!.groupValues
        Valve(name, rate.toInt(), l.split(", "))
    }.associateBy { it.name }

    val graph = mutableMapOf<Valve, MutableSet<Valve>>()
    val stack = LinkedList<Valve>()

    val start = valves["AA"]!!
    stack.add(start)
    while (stack.isNotEmpty()) {
        val current = stack.pop()
        val children = current.tunnels.map { valves[it]!! }
        val l = graph.getOrPut(current) { mutableSetOf() }
        l.addAll(children)
        stack.addAll(children.filter { it !in graph })
    }

    val optimizedGraph = mutableMapOf<Valve, MutableMap<Valve, Int>>()
    for (n in graph) {
        optimizedGraph[n.key] = n.value.associateWith { 1 }.toMutableMap()
    }

    for (k in optimizedGraph.keys) {
        for (i in optimizedGraph.keys) {
            for (j in optimizedGraph.keys) {
                val ik = optimizedGraph[i]?.get(k) ?: inf
                val kj = optimizedGraph[k]?.get(j) ?: inf
                val ij = optimizedGraph[i]?.get(j) ?: inf
                if (ik + kj < ij) {
                    val m = optimizedGraph[i]!!
                    m[j] = ik + kj
                }
            }
        }
    }

    optimizedGraph.values.forEach {
        it.keys.filter { v -> v.rate == 0 }
            .forEach { toRemove -> it.remove(toRemove) }
    }

    return optimizedGraph
}

