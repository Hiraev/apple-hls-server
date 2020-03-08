import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import model.Headers
import model.Request
import model.Response
import java.net.ServerSocket
import java.util.concurrent.Executors

class Server(
    port: Int,
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
            CoroutineScope(Dispatchers.Default).launch {
                try {
                    val request = read(socket.getInputStream())
                    val response = router.invoke(request)
                    response.headers.add(Headers.CONTENT_LENGTH to response.body.size.toString())
                    write(socket.getOutputStream(), response)
                } catch (e: RuntimeException) {
                    e.printStackTrace()
                }
            }
        }
    }

}
