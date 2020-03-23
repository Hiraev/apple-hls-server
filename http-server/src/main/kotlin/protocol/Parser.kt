package protocol

import exceptions.UnsupportedHttpMethodException
import model.Method
import model.TopHeader
import java.net.URI

fun parseTopHeader(string: String): TopHeader {
    val words = string.split(" ").map(String::trim).filterNot { it.isEmpty() }
    val method = when (words.component1().toUpperCase()) {
        "GET" -> Method.GET
        "POST" -> Method.POST
        else -> throw UnsupportedHttpMethodException(words.joinToString())
    }
    val uri = URI(words.component2())
    return TopHeader(method, uri, words.component3())
}

fun parseHeader(string: String): Pair<String, String> {
    val headerElements = string.split(":").map(String::trim)
    return Pair(headerElements.component1(), headerElements.component2())
}
