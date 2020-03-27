import model.Body
import model.Request
import model.Response
import model.Responses
import router.Matcher
import router.fullMatcher
import java.io.File

lateinit var common: HlsServerCommon

fun main(args: Array<String>) {
    common = HlsServerCommon(args)

    Server(common.port) {
        GET("/".fullMatcher()) {
            tryFile(common.root, "/index.html")
        }
        GET(Matcher.NoMatcher) { request ->
            tryFile(common.root, request.uri.path)
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

fun tryFile(root: File, path: String): Response = try {
    Response.fromFile(File(root.path + path))
} catch (e: Throwable) {
    e.printStackTrace()
    Responses.notFound
}

fun loadMp4(request: Request): Response {
    request.body.let { it as? Body.ArrayBody }?.byteArray?.let(common::saveVideo)
    return Responses.redirectTo("/index.html")
}
