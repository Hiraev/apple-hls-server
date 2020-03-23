package protocol

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import model.Body
import model.Response
import model.constants.HttpConstants
import java.io.OutputStream

suspend fun write(outputStream: OutputStream, response: Response) = withContext(Dispatchers.IO) {
    val headers = StringBuilder()
            .append(response.httpVersion)
            .append(" ")
            .append(response.code.toString())
            .append(" ")
            .append(response.message)
            .append(HttpConstants.CRLF)
            .apply {
                if (response.headers.isNotEmpty()) {
                    this.append(response.headers)
                            .append(HttpConstants.CRLF)
                }
            }
            .toString()
            .toByteArray()

    outputStream.write(headers)
    if (response.body is Body.ArrayBody) {
        outputStream.write(response.body.byteArray)
    }
}
