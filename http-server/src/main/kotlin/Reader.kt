import exceptions.BadRequestException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import model.Headers
import model.Request
import utils.ByteArrayUtils
import java.io.InputStream

private const val CR = '\r'
private const val LF = '\n'
private const val EOF = (-1).toChar()

suspend fun read(inputStream: InputStream): Request {
    // Read top line
    val topLine = readLine(inputStream)
    if (topLine.isEmpty()) throw BadRequestException()
    val top = parseTopHeader(topLine)

    // Read headers
    val headers = Headers()
    while (true) {
        val line = readLine(inputStream)
        if (line.isEmpty()) break
        headers.add(parseHeader(line))
    }

    // Check headers
    val contentLength = headers.get(Headers.CONTENT_LENGTH)?.toInt()
    val isChunked = headers.get(Headers.TRANSFER_ENCODING)?.contains(Headers.TRANSFER_ENCODING_CHUNKED) == true

    var body: ByteArray? = null

    if (contentLength != null && contentLength != 0) {
        body = readBody(inputStream, contentLength)
    } else if (isChunked) {
        body = readChunkedBody(inputStream)
    }
    return Request(top.method, top.path, headers, body)
}

suspend fun readLine(inputStream: InputStream): String = withContext(Dispatchers.IO) {
    val lineBuilder = StringBuilder()

    var prevSym = inputStream.read().toChar()
    if (prevSym == EOF) return@withContext ""
    var nextSym = inputStream.read().toChar()
    if (nextSym == EOF) return@withContext ""

    while (true) {
        if (prevSym == CR && nextSym == LF) {
            break
        } else if (prevSym != CR) {
            lineBuilder.append(prevSym)
        }
        prevSym = nextSym
        nextSym = inputStream.read().toChar()
    }

    lineBuilder.toString()
}

suspend fun readBody(inputStream: InputStream, size: Int) = withContext(Dispatchers.IO) {
    val byteArray = ByteArray(size)
    inputStream.read(byteArray, 0, size)
    byteArray
}

suspend fun readChunkedBody(inputStream: InputStream): ByteArray {
    var chunkSize: Int
    val chunks = mutableListOf<ByteArray>()
    do {
        chunkSize = readLine(inputStream).toInt()
        chunks += readBody(inputStream, chunkSize)
        readLine(inputStream) // Empty line
    } while (chunkSize != 0)
    return ByteArrayUtils.merge(chunks)
}
