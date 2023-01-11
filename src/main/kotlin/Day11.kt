package d11

import DaySolver
import java.util.*

class Day11 : DaySolver(11, "Monkey in the Middle") {
    override val exampleInput = """
        Monkey 0:
          Starting items: 79, 98
          Operation: new = old * 19
          Test: divisible by 23
            If true: throw to monkey 2
            If false: throw to monkey 3

        Monkey 1:
          Starting items: 54, 65, 75, 74
          Operation: new = old + 6
          Test: divisible by 19
            If true: throw to monkey 2
            If false: throw to monkey 0

        Monkey 2:
          Starting items: 79, 60, 97
          Operation: new = old * old
          Test: divisible by 13
            If true: throw to monkey 1
            If false: throw to monkey 3

        Monkey 3:
          Starting items: 74
          Operation: new = old + 3
          Test: divisible by 17
            If true: throw to monkey 0
            If false: throw to monkey 1
    """.trimIndent()

    private val aggregatedData = input.splitToSequence("\n\n").map { it.toMonkey() }

    override fun firstPart(): String {
        val monkeys = aggregatedData.toList()
        repeat(20) {
            for (monkey in monkeys) {
                monkey.turn(monkeys)
            }
        }

        val (f, s) = monkeys.sortedByDescending { it.inspected }.take(2)

        return (f.inspected * s.inspected).toString()
    }

    override fun secondPart(): String {
        val monkeys = aggregatedData.toList()
        val n = monkeys.fold(1L) { a, x -> a * x.test.divisible}
        repeat(10_000) {
            for (monkey in monkeys) {
                monkey.turn2(monkeys, n)
            }
        }

        val (f, s) = monkeys.sortedByDescending { it.inspected }.take(2)

        return (f.inspected * s.inspected).toString()
    }
}

data class Monkey(val n: Int, val startingItems: LinkedList<Long>, val operation: Operation, val test: Test) {
    var inspected = 0L
    fun turn(monkeys: List<Monkey>) {
        while (startingItems.isNotEmpty()) {
            val item = startingItems.removeLast()
            val worry = operation.compute(item) / 3
            monkeys[test.test(worry)].startingItems.addLast(worry)
            inspected++
        }
    }

    fun turn2(monkeys: List<Monkey>, n: Long) {
        while (startingItems.isNotEmpty()) {
            val item = startingItems.removeLast()
            val worry = operation.compute(item)
            val w = worry % n
            monkeys[test.test(worry)].startingItems.addLast(w)
            inspected++
        }
    }
}

data class Operation(val left: Expression, val op: Op, val right: Expression) {
    fun compute(old: Long): Long {
        return op.op(left.new(old), right.new(old))
    }
}

data class Test(val divisible: Long, val ifTrue: Int, val ifFalse: Int) {
    fun test(item: Long): Int {
        return if (item % divisible == 0L) ifTrue
        else ifFalse
    }
}

enum class Op(val op: (Long, Long) -> Long) {
    TIMES({ a, b -> a * b }), PLUS({ a, b -> a + b });
}

sealed interface Expression {
    fun new(old: Long): Long
}

object Old : Expression {
    override fun new(old: Long): Long {
        return old
    }
}

data class Literal(val value: Long) : Expression {
    override fun new(old: Long): Long {
        return value
    }
}

val monkeyNumberRegex = Regex("\\s*Monkey (\\d+):")
val monkeyItemsRegex = Regex("\\s*Starting items: (.+)")
val operationRegex = Regex("\\s*Operation: new = (old|\\d+) (.) (old|\\d+)")
val divisibleRegex = Regex("\\s*Test: divisible by (\\d+)")
val testRegex = Regex("\\s*If \\w+: throw to monkey (\\d+)")

fun String.toMonkey(): Monkey {
    val lines = this.lines()
    val n = monkeyNumberRegex.matchEntire(lines[0])!!.groupValues[1].toInt()
    val items = LinkedList(monkeyItemsRegex.matchEntire(lines[1])!!.groupValues[1].split(", ").map { it.toLong() })

    val (_, left, op, right) = operationRegex.matchEntire(lines[2])!!.groupValues
    val operation = Operation(left.toExpression(), op.toOp(), right.toExpression())

    val divisible = divisibleRegex.matchEntire(lines[3])!!.groupValues[1].toLong()
    val ifTrue = testRegex.matchEntire(lines[4])!!.groupValues[1].toInt()
    val ifFalse = testRegex.matchEntire(lines[5])!!.groupValues[1].toInt()
    val test = Test(divisible, ifTrue, ifFalse)

    return Monkey(n, items, operation, test)
}

fun String.toExpression(): Expression {
    return when (this) {
        "old" -> Old
        else -> Literal(this.toLong())
    }
}

fun String.toOp(): Op {
    return when (this) {
        "*" -> Op.TIMES
        "+" -> Op.PLUS
        else -> error("not an enum")
    }
}
