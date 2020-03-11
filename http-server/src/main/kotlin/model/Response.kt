package model

data class Response(
    val code: Int,
    val message: String,
    val httpVersion: String = "HTTP/2.0",
    val headers: Headers = Headers(),
    val body: ByteArray = ByteArray(0)
) {

    companion object {
        val OK = Response(200, "OK")
        val NOT_FOUND = Response(404, "Not Found")
        val METHOD_NOT_ALLOWED = Response(405, "Method Not Allowed")

        fun redirectTo(path: String) = Response(301, "Redirect").apply {
            headers.add("Location" to path)
        }
    }

}
