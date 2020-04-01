package model

import java.net.URI

class Request(
        val method: Method,
        val uri: URI,
        headers: Headers,
        body: Body
) : Call(headers, body) {

    val parameters: Map<String, String> = uri.query
            ?.split("&")
            ?.map { paramPair ->
                paramPair.split("=")
                        .let { it.component1() to it.component2() }
            }?.toMap()
            ?: emptyMap()

}
