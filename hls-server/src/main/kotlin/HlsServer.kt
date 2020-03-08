import model.Response

fun main() {
    Server(80) { request ->
        when (request.uri.path) {
            "/" -> Response(200, "OK", body = "Hi".toByteArray())
            else -> Response(404, "Not Found")
        }
    }.start()
}
