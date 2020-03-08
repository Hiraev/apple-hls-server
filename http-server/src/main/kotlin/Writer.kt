import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import model.Response
import java.io.OutputStream

private const val CR = '\r'
private const val LF = '\n'
private const val CRLF = "$CR$LF"

suspend fun write(outputStream: OutputStream, response: Response) = withContext(Dispatchers.IO) {
    val headers = StringBuilder()
        .append(response.httpVersion)
        .append(" ")
        .append(response.code.toString())
        .append(" ")
        .append(response.message)
        .append(CRLF)
        .apply {
            if (response.headers.isNotEmpty()) {
                this.append(response.headers)
                    .append(CRLF)
            }
        }
        .toString()
        .toByteArray()

    outputStream.write(headers)
    if (response.body.isNotEmpty()) {
        outputStream.write(response.body)
    }
}
