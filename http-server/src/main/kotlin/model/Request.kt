package model

import java.net.URI

data class Request(
    val uri: URI,
    val headers: Headers,
    val body: ByteArray?
)
