import io.ktor.application.call
import io.ktor.http.content.forEachPart
import io.ktor.request.contentType
import io.ktor.request.document
import io.ktor.request.isChunked
import io.ktor.request.receiveMultipart
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

fun main(args: Array<String>) {
    embeddedServer(Netty, 80) {
        routing {
            post("/") {
                call.receiveMultipart().forEachPart {  }
            }
        }
    }
}