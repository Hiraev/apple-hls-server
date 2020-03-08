package model

import java.net.URI

data class TopHeader(
    val method: Method,
    val path: URI,
    val version: String
)
