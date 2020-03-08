package model

import java.net.URI

data class Request(
    val method: Method,
    val uri: URI,
    val headers: Headers,
    val body: ByteArray?
)
