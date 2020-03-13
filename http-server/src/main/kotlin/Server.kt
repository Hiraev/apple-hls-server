import extensions.isKeepAlive
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import model.Headers
import model.Request
import model.Response
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.Executors

class Server(
    port: Int,
    private val timeout: Int = 15000,
    private val router: suspend (Request) -> Response
) {

    private val socket = ServerSocket(port)
    private val acceptor = Executors.newSingleThreadExecutor()

    private var enabled = true

    fun start() {
        socket.reuseAddress = true
        acceptor.submit(::loop)
    }

    private fun loop() {
        while (enabled) {
            val socket = socket.accept()
            socket.soTimeout = timeout
            handle(socket)
        }
    }

    private fun handle(socket: Socket) {
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val request = read(socket.getInputStream())
                val response = router.invoke(request)
                response.headers.add(Headers.CONTENT_LENGTH to response.body.size.toString())
                write(socket.getOutputStream(), response)
                if (request.headers.isKeepAlive()) handle(socket)
                else socket.close()
            } catch (e: RuntimeException) {
                e.printStackTrace()
            }
        }
    }

}
