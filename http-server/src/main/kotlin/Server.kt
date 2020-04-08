import extensions.isKeepAlive
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import protocol.read
import protocol.write
import router.Router
import java.net.ServerSocket
import java.net.Socket
import java.util.UUID
import java.util.concurrent.Executors

class Server(
        port: Int,
        private val timeout: Int = 15000,
        private val router: Router.() -> Unit
) {

    private val socket = ServerSocket(port)
    private val acceptor = Executors.newSingleThreadExecutor()
    private val routerObj = Router()

    private var enabled = true

    fun start() {
        socket.reuseAddress = true
        router.invoke(routerObj)
        acceptor.submit(::loop)
    }

    private fun loop() {
        while (enabled) {
            val socket = socket.accept()
            socket.soTimeout = timeout
            handle(socket, UUID.randomUUID())
        }
    }

    private fun handle(socket: Socket, uuid: UUID) {
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val request = read(socket.getInputStream())
                val response = routerObj.handle(request)

                write(socket.getOutputStream(), response)
                if (request.headers.isKeepAlive() && response.headers.isKeepAlive()) {
                    handle(socket, uuid)
                } else if (!socket.isClosed) {
                    socket.close()
                }
            } catch (e: Throwable) {
                println("[$uuid] Caught: ${e.javaClass.name} ${e.message}")
            }
        }
    }

}
