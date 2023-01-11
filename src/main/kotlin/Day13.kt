package d13

import DaySolver

class Day13 : DaySolver(13, "Distress Signal") {
    override val exampleInput = """
        [1,1,3,1,1]
        [1,1,5,1,1]
        
        [[1],[2,3,4]]
        [[1],4]
        
        [9]
        [[8,7,6]]
        
        [[4,4],4,4]
        [[4,4],4,4,4]
        
        [7,7,7,7]
        [7,7,7]
        
        []
        [3]
        
        [[[]]]
        [[]]
        
        [1,[2,[3,[4,[5,6,7]]]],8,9]
        [1,[2,[3,[4,[5,6,0]]]],8,9]
    """.trimIndent()

    private val aggregatedData = build(input)

    override fun firstPart(): String {
        val result = aggregatedData.withIndex().filter { check(it.value) }.sumOf { it.index+1 }

        return result.toString()
    }

    override fun secondPart(): String {
        val t1 = TreeImpl(null, mutableListOf(TreeImpl(null, mutableListOf(Value(2)))))
        val t2 = TreeImpl(null, mutableListOf(TreeImpl(null, mutableListOf(Value(6)))))
        val s = (aggregatedData.flatMap { it.toList() }+t1+t2).sorted()

        return ((s.indexOf(t1)+1)*(s.indexOf(t2)+1)).toString()
    }
}
fun build(s: String): List<Pair<Tree, Tree>> {
    return s.split("\n\n").map {
        val (p1, p2) = it.lines()
        parse(p1) to parse(p2)
    }
}

fun check(p: Pair<Tree, Tree>): Boolean {
    val (left, right) = p
    return left < right // ?: error("impossible given the inputs")
}

val regexInt = Regex("(\\d*)")

fun parse(line: String): Tree {
    var i = 0
    var currentTree: TreeImpl? = null
    while(i < line.length) {
        when(line[i]) {
            '[' -> {
                val new = TreeImpl(currentTree)
                currentTree?.children?.add(new)
                currentTree = new
                i++
            }
            ']' -> {
                if (currentTree?.parent != null)
                    currentTree = currentTree.parent
                i++
            }
            ',' -> i++
            else -> {
                val result = regexInt.matchAt(line, i)!!
                val (entire, integer) = result.groupValues
                if (integer.isNotEmpty()) {
                    currentTree!!.children.add(Value(integer.toInt()))
                    i += entire.length
                }
            }
        }
    }
    return currentTree!!
}

sealed interface Tree : Comparable<Tree>

data class TreeImpl(val parent: TreeImpl?, val children: MutableList<Tree> = mutableListOf()) : Tree {
    override fun compareTo(other: Tree): Int {
        return when(other) {
            is TreeImpl -> children.compareTo(other.children)
            is Value -> this.compareTo(other.toTree())
        }
    }

    override fun toString(): String {
        return "T$children"
    }
}
data class Value(val v: Int) : Tree {
    override fun compareTo(other: Tree): Int {
        return when (other) {
            is Value -> v.compareTo(other.v)
            is TreeImpl -> this.toTree().compareTo(other)
        }
    }

    fun toTree(): TreeImpl {
        return TreeImpl(null, mutableListOf(Value(v)))
    }


    override fun toString(): String {
        return "$v"
    }
}

fun List<Tree>.compareTo(l: List<Tree>): Int {
    for (i in 0 until minOf(size, l.size)) {
        val elem1 = get(i)
        val elem2 = l[i]

        val c = elem1.compareTo(elem2)
        if (c != 0) return c
    }
    return size.compareTo(l.size)
}
