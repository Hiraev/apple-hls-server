import model.Body
import model.Headers
import model.Request
import model.Response
import model.Responses
import model.constants.HttpCodeAndMessage
import model.constants.HttpConstants
import model.constants.Mime
import router.Matcher
import router.fullMatcher
import java.io.File
import java.util.UUID

const val dirName = "uploaded/videos"

lateinit var args_: Args

fun main(args: Array<String>) {
    val parsedArgs = ArgsParser.parse(args)
    val root = File(parsedArgs.root)
    require(root.isDirectory)

    args_ = parsedArgs

    Server(parsedArgs.port) {
        GET("/".fullMatcher()) {
            tryFile(root, "/index.html")
        }
        GET(Matcher.NoMatcher) { request ->
            tryFile(root, request.uri.path)
        }
        POST("/upload/video".fullMatcher()) { request ->
            loadMp4(request)
        }
        setPathNotFoundAction {
            Responses.notFound
        }
        setMethodNotAllowedAction {
            Responses.methodNotAllowed
        }
    }.start()
}

fun saveVideo(byteArray: ByteArray) {
    val fileName = UUID.randomUUID()
    val dirPath = args_.root + "/" + dirName + "/" + fileName
    val dir = File(dirPath)
    dir.mkdirs()

    val file = File("$dirPath/$fileName.mp4")
    file.createNewFile()
    file.writeBytes(byteArray)
    println("Video saved")
}

fun tryFile(root: File, path: String): Response = try {
    val file = File(root.path + path)
    val bytes = file.readBytes()

    val headers = Headers()
    extensionToMimeType(file.extension)?.let { mimeType -> headers.add(HttpConstants.Headers.CONTENT_TYPE to mimeType) }

    HttpCodeAndMessage.OK.toResponse().copy(body = Body.ArrayBody(bytes), headers = headers)
} catch (e: Throwable) {
    e.printStackTrace()
    Responses.notFound
}

fun loadMp4(request: Request): Response {
    request.body.let { it as? Body.ArrayBody }?.byteArray?.let(::saveVideo)
    return Responses.redirectTo("/index.html")
}

fun extensionToMimeType(extension: String): String? = when (extension) {
    "m3u8" -> Mime.M3U8
    "ts" -> Mime.TS
    "html" -> Mime.HTML
    "css" -> Mime.CSS
    else -> null
}
