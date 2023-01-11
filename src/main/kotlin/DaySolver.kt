import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import java.io.File
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime


// Gratuitously stolen from Khatharsis repo, but tweaked for jvm Kotlin
open class DaySolver(val day: Int, val name: String) {
    open fun firstPart(): String = "First part isn't done yet."
    open fun secondPart(): String = "Second part isn't done yet."

    @OptIn(ExperimentalTime::class)
    fun solve() {
        val time = measureTime {
            println("Solving for day $day: $name")
            println("   Part 1: ${firstPart()}")
            println("   Part 2: ${secondPart()}")
        }
        println("   Time taken: $time")
    }

    open val exampleInput: String = ""

    val input: String = runBlocking {
        val f = File("inputs/$day")
        if (f.exists()) {
            f.readText()
        } else {
            val response = client.get("https://adventofcode.com/2022/day/$day/input") {
                header("User-Agent", "alexandre.conte11@gmail.com")
            }
            val content = response.bodyAsText().trimEnd()
            f.parentFile.mkdir()
            f.writeText(content)
            content
        }
    }

    companion object {
        val client = HttpClient(CIO) {
            install(HttpCookies) {
                val cookie = DaySolver::class.java.getResource("cookie")?.readText()
                    ?: error("The cookie file named cookie can not be found in the resources folder")
                storage = ConstantCookiesStorage(Cookie("session", cookie, domain = "adventofcode.com"))
            }
        }
    }
}
