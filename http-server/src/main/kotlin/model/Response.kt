package model

class Response(
    val code: Int,
    val message: String,
    val httpVersion: String = "HTTP/2.0",
    val headers: Headers = Headers(),
    val body: ByteArray = ByteArray(0)
)
