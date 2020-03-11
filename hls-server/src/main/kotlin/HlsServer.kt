import model.Headers
import model.Method
import model.Request
import model.Response
import java.io.File

fun main(args: Array<String>) {
    val parsedArgs = ArgsParser.parse(args)
    val root = File(parsedArgs.root)
    require(root.isDirectory)

    Server(parsedArgs.port) { request ->
        when (request.method) {
            Method.GET -> when (request.uri.path) {
                "/" -> Response.OK
                else -> tryFile(root, request.uri.path)
            }
            Method.POST -> when (request.uri.path) {
                "/upload/video" -> {
                    loadMp4(request)
                }
                else -> {
                    Response.NOT_FOUND
                }
            }
            else -> Response.METHOD_NOT_ALLOWED
        }
    }.start()
}

fun tryFile(root: File, path: String): Response = try {
    val file = File(root.path + path)
    val bytes = file.readBytes()

    val headers = Headers()
    extensionToMimeType(file.extension)?.let { mimeType -> headers.add(Headers.CONTENT_TYPE to mimeType) }

    Response.OK.copy(body = bytes, headers = headers)
} catch (e: Throwable) {
    e.printStackTrace()
    Response.NOT_FOUND
}

fun loadMp4(request: Request): Response {
    println("load mpeg-4")
    println(request.uri)
    println("loaded file size: ${request.body?.size}")
    return Response.redirectTo("/index.html").apply {

    }
}

fun extensionToMimeType(extension: String): String? = when (extension) {
    "m3u8" -> Headers.CONTENT_TYPE_M3U8
    "ts" -> Headers.CONTENT_TYPE_TS
    "html" -> Headers.CONTENT_TYPE_HTML
    "css" -> Headers.CONTENT_TYPE_CSS
    else -> null
}