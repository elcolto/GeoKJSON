@file:Repository("https://repo1.maven.org/maven2")
@file:DependsOn("com.squareup.moshi:moshi-kotlin:1.14.0")

import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.io.File
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Script to copy issues from a repository and  https://cli.github.com/
 */

@JsonClass(generateAdapter = true)
data class Issue(
    val number: Int,
    val title: String,
    val body: String,
)

// gh issue list -L 150 --repo dellisd/spatial-k --json body,closed,labels,state,title > issues.json
val content = Runtime
    .getRuntime()
    .exec("gh issue list -L 150 --repo dellisd/spatial-k --json body,closed,labels,state,title,number")
    .inputStream
    .bufferedReader()
    .readText()

val moshi = Moshi.Builder()
    .addLast(KotlinJsonAdapterFactory())
    .build()

@OptIn(ExperimentalStdlibApi::class)
val issues = moshi
    .adapter<List<Issue>>()
    .fromJson(content)!!

val scanner = Scanner(System.`in`)
issues.forEach { issue ->
    println("#${issue.number}: ${issue.title}")
    println(issue.body.take(100))
    println("Transfer this issue? y/n")
    val input = scanner.next().lowercase()
    if (input == "y") {
        println("Creating issue to Project...")
        val command = "gh issue create --repo elcolto/GeoKJSON --title \"${issue.title}\" --body \"${issue.body}\""
        println(command)
        println(command.count { it == '\n' })
        val process = Runtime.getRuntime().exec(command)
        if (process.waitFor() == 0) {
            println("Success:")
            println(process.inputReader().readText())
        } else {
            println("Error:")
            println(process.errorReader().readText())
        }
    } else {
        println("Skipping this issue")
    }
}
