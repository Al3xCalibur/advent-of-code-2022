package d10

import DaySolver
import java.lang.StringBuilder

class Day10 : DaySolver(10, "Cathode-Ray Tube") {
    override val exampleInput = """
        addx 15
        addx -11
        addx 6
        addx -3
        addx 5
        addx -1
        addx -8
        addx 13
        addx 4
        noop
        addx -1
        addx 5
        addx -1
        addx 5
        addx -1
        addx 5
        addx -1
        addx 5
        addx -1
        addx -35
        addx 1
        addx 24
        addx -19
        addx 1
        addx 16
        addx -11
        noop
        noop
        addx 21
        addx -15
        noop
        noop
        addx -3
        addx 9
        addx 1
        addx -3
        addx 8
        addx 1
        addx 5
        noop
        noop
        noop
        noop
        noop
        addx -36
        noop
        addx 1
        addx 7
        noop
        noop
        noop
        addx 2
        addx 6
        noop
        noop
        noop
        noop
        noop
        addx 1
        noop
        noop
        addx 7
        addx 1
        noop
        addx -13
        addx 13
        addx 7
        noop
        addx 1
        addx -33
        noop
        noop
        noop
        addx 2
        noop
        noop
        noop
        addx 8
        noop
        addx -1
        addx 2
        addx 1
        noop
        addx 17
        addx -9
        addx 1
        addx 1
        addx -3
        addx 11
        noop
        noop
        addx 1
        noop
        addx 1
        noop
        noop
        addx -13
        addx -19
        addx 1
        addx 3
        addx 26
        addx -30
        addx 12
        addx -1
        addx 3
        addx 1
        noop
        noop
        noop
        addx -9
        addx 18
        addx 1
        addx 2
        noop
        noop
        addx 9
        noop
        noop
        noop
        addx -1
        addx 2
        addx -37
        addx 1
        addx 3
        noop
        addx 15
        addx -21
        addx 22
        addx -6
        addx 1
        noop
        addx 2
        addx 1
        noop
        addx -10
        noop
        noop
        addx 20
        addx 1
        addx 2
        addx 2
        addx -6
        addx -11
        noop
        noop
        noop
    """.trimIndent()

    private val aggregatedData = input.lineSequence().map { Instruction.fromString(it) }.toList()

    override fun firstPart(): String {
        var cycle = 0
        var x = 1
        var acc = 0
        for (instr in aggregatedData) {
            repeat(instr.countCycle) {
                cycle++
                if ((cycle - 20) % 40 == 0 && cycle <= 220) {
                    acc += cycle * x
                }
            }
            x = instr.endCycle(x)
        }
        return acc.toString()
    }

    override fun secondPart(): String {
        var cycle = 0
        var x = 1
        val result = StringBuilder("\n")
        for (instr in aggregatedData) {
            repeat(instr.countCycle) {
                if ((cycle%40) in x-1..x+1)
                    result.append("#")
                else
                    result.append(".")
                if (cycle % 40 == 39) {
                    result.append("\n")
                }
                cycle++
            }
            x = instr.endCycle(x)
        }
        return result.toString()
    }
}

sealed interface Instruction {
    val countCycle: Int
    fun endCycle(previous: Int): Int
    companion object {
        fun fromString(s: String): Instruction {
            return if (s == "noop") Noop
            else {
                val (_, n) = s.split(" ")
                Addx(n.toInt())
            }
        }
    }
}

object Noop : Instruction {
    override val countCycle: Int = 1

    override fun endCycle(previous: Int): Int = previous
}

data class Addx(val n: Int) : Instruction {
    override val countCycle: Int = 2

    override fun endCycle(previous: Int): Int = previous + n
}
