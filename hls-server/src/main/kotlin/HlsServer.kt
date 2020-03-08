import model.Headers
import model.Method
import model.Response
import java.io.File

fun main(args: Array<String>) {
    val parsedArgs = ArgsParser.parse(args)
    val root = File(parsedArgs.root)
    require(root.isDirectory)

    Server(parsedArgs.port) { request ->
        when (request.method) {
            Method.GET -> when (request.uri.path) {
                "/" -> Response(200, "OK")
                else -> tryFile(root, request.uri.path)
            }
            Method.POST -> {
                Response(404, "Not Found")
            }
        }
    }.start()
}

fun tryFile(root: File, path: String): Response = try {
    val bytes = File(root.path + path).readBytes()
    val headers = Headers()
    if (path.endsWith("m3u8")) {
        headers.add(Headers.CONTENT_TYPE to Headers.CONTENT_TYPE_M3U8)
    } else if (path.endsWith("ts")) {
        headers.add(Headers.CONTENT_TYPE to Headers.CONTENT_TYPE_TS)
    }
    Response(200, "OK", body = bytes, headers = headers)
} catch (e: Throwable) {
    e.printStackTrace()
    Response(404, "Not Found")
}
