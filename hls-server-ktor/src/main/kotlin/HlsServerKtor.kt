import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.request.header
import io.ktor.request.path
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondFile
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import java.io.File

fun main(args: Array<String>) {
    val common = HlsServerCommon(args)
    embeddedServer(Netty, common.port) {
        routing {
            get("/") {
                call.respondText(common.getIndexPage(), contentType = ContentType.parse("text/html"))
            }
            get("/*") {
                val path = call.request.path()
                call.respondFile(File(common.root.path + path))
            }
            get("/delete/video") {
                call.request.queryParameters["id"]?.let {
                    common.removeVideo(it)
                    call.respond(HttpStatusCode.OK)
                } ?: call.respond(HttpStatusCode.NotFound)
            }
            get("/uploaded/videos/*/m3u8/*") {
                val path = call.request.path()
                call.respondFile(File(common.root.path + path))
            }
            post("/upload/video") {
                require("video/mp4".equals(call.request.header("Content-Type"), ignoreCase = true))
                val videoByteArray = call.receive<ByteArray>()
                common.saveVideo(videoByteArray)
                call.respond(HttpStatusCode.OK)
            }
        }
    }.start()
}
