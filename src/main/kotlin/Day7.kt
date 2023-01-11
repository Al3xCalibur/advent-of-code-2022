package d7

import DaySolver

class Day7 : DaySolver(7, "No Space Left On Device") {
    override val exampleInput: String = """
        $ cd /
        $ ls
        dir a
        14848514 b.txt
        8504156 c.dat
        dir d
        $ cd a
        $ ls
        dir e
        29116 f
        2557 g
        62596 h.lst
        $ cd e
        $ ls
        584 i
        $ cd ..
        $ cd ..
        $ cd d
        $ ls
        4060174 j
        8033020 d.log
        5626152 d.ext
        7214296 k
    """.trimIndent()

    private val aggregatedData = build(input).also { it.size() }

    override fun firstPart(): String {
        val result = aggregatedData.getDirectoryBelowThreshold().sumOf { it.size }

        return result.toString()
    }

    override fun secondPart(): String {
        val neededSpace = 30000000 - (70000000 - aggregatedData.size())

        return aggregatedData.getDirectoryAboveThreshold(neededSpace).minBy { it.size }.size.toString()
    }
}


sealed interface FileSystemObject {
    fun size(): Long
    fun getDirectoryBelowThreshold(): List<Directory>
    fun getDirectoryAboveThreshold(limit: Long): List<Directory>
}

class Directory(val name: String, val parent: Directory? = null) : FileSystemObject {
    val children = mutableListOf<FileSystemObject>()
    var size: Long = -1

    // To be launched only when the system is build completely (because of lazy values)
    override fun size(): Long {
        if (size == -1L) {
            size = children.sumOf { it.size() }
        }
        return size
    }

    override fun getDirectoryBelowThreshold(): List<Directory> {
        return if (size <= 100000)
            listOf(this) + children.flatMap { it.getDirectoryBelowThreshold() }
        else children.flatMap { it.getDirectoryBelowThreshold() }
    }

    override fun getDirectoryAboveThreshold(limit: Long): List<Directory> {
        return if (size >= limit)
            listOf(this) + children.flatMap { it.getDirectoryAboveThreshold(limit) }
        else emptyList()
    }

    fun searchDirectory(name: String): Directory {
        return children.filterIsInstance<Directory>().first { it.name == name }
    }
}

class File(val name: String, val size: Long) : FileSystemObject {
    override fun size(): Long = size
    override fun getDirectoryBelowThreshold(): List<Directory> = emptyList()
    override fun getDirectoryAboveThreshold(limit: Long): List<Directory> = emptyList()
}

class CD(val name: String) {
    fun isParent() = name == ".."

    companion object {
        val regex = Regex("\n?\\$ cd (.+)\n")

        fun fromString(str: String): Pair<CD?, Int> {
            val matches = regex.matchAt(str, 0) ?: return null to 0
            val (entire, name) = matches.groupValues
            return CD(name) to entire.length
        }
    }
}

class LS(val lines: List<LSLine>) {

    // to be removed
    sealed interface LSLine
    class Dir(val name: String) : LSLine
    class File(val name: String, val size: Long) : LSLine

    companion object {
        val regex = Regex("\n?\\$ ls\n([^$]+)")

        fun fromString(str: String): Pair<LS?, Int> {
            val matches = regex.matchAt(str, 0) ?: return null to 0
            val (entire, lines) = matches.groupValues
            return LS(lines.lineSequence().filterNot { it.isBlank() }.map { stringToLine(it) }.toList()) to entire.length
        }

        fun stringToLine(line: String): LSLine {
            val (sizeOrDir, name) = line.split(" ")
            return if (sizeOrDir == "dir")
                Dir(name)
            else
                File(name, sizeOrDir.toLong())
        }
    }
}

fun build(str: String): FileSystemObject {
    val (r, n) = CD.fromString(str)
    val root = Directory(r!!.name)
    var currentDirectory = root
    var s = str.substring(n)

    while (s.isNotEmpty()) {
        val (cd, nc) = CD.fromString(s)
        val (ls, nl) = LS.fromString(s)

        if (cd != null) {
            currentDirectory =
                if (cd.isParent()) currentDirectory.parent!!
                else currentDirectory.searchDirectory(cd.name)
            s = s.substring(nc)
        } else if (ls != null) {
            currentDirectory.children.addAll(ls.lines.map {
                when(it) {
                    is LS.Dir -> Directory(it.name, currentDirectory)
                    is LS.File -> File(it.name, it.size)
                }
            })
            s = s.substring(nl)
        } else error("not acceptable")
        }
    return root
}



