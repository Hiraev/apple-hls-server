import model.Body
import model.Request
import model.Response
import model.Responses
import model.constants.HttpConstants
import model.constants.Mime
import router.Matcher
import router.fullMatcher
import router.prefixMatcher
import java.io.File
import java.nio.charset.Charset

lateinit var common: HlsServerCommon

fun main(args: Array<String>) {
    common = HlsServerCommon(args)

    Server(common.port) {
        GET("/".fullMatcher()) {
            Responses.ok().copy(
                    body = Body.ArrayBody(common.getIndexPage().toByteArray(Charset.defaultCharset()))
            ).apply {
                headers.add(HttpConstants.Headers.CONTENT_TYPE to "${Mime.HTML}; charset=UTF-8")
            }
        }
        GET("/delete/video".prefixMatcher()) {
            it.parameters["id"]?.let { id ->
                common.removeVideo(id)
                Responses.ok().copy()
            } ?: Responses.notFound()
        }
        GET(Matcher.Else) { request ->
            tryFile(common.root, request.uri.path)
        }
        POST("/upload/video".fullMatcher()) { request ->
            loadMp4(request)
        }
        setPathNotFoundAction {
            Responses.notFound()
        }
        setMethodNotAllowedAction {
            Responses.methodNotAllowed()
        }
    }.start()
}

fun tryFile(root: File, path: String): Response = try {
    Response.fromFile(File(root.path + path))
} catch (e: Throwable) {
    e.printStackTrace()
    Responses.notFound()
}

fun loadMp4(request: Request): Response {
    request.body.let { it as? Body.ArrayBody }?.byteArray?.let(common::saveVideo)
    return Responses.ok()
}
