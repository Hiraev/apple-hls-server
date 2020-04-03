package protocol

import exceptions.BadRequestException
import extensions.contentLength
import extensions.isChunked
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import model.Body
import model.Headers
import model.Request
import model.constants.HttpConstants
import utils.ByteArrayUtils
import java.io.EOFException
import java.io.InputStream

internal suspend fun read(inputStream: InputStream): Request {
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
    val contentLength = headers.contentLength()
    val isChunked = headers.isChunked()

    var body: Body = Body.Empty

    if (contentLength != null && contentLength != 0) {
        body = Body.ArrayBody(readBody(inputStream, contentLength))
    } else if (isChunked) {
        body = Body.ArrayBody(readChunkedBody(inputStream))
    }

    return Request(top.method, top.path, headers, body)
}

internal suspend fun readLine(inputStream: InputStream): String = withContext(Dispatchers.IO) {
    val lineBuilder = StringBuilder()

    var prevSym = inputStream.read().toChar()
    if (prevSym == HttpConstants.EOF) throw EOFException()
    var nextSym = inputStream.read().toChar()
    if (nextSym == HttpConstants.EOF) throw EOFException()

    while (true) {
        if (prevSym == HttpConstants.CR && nextSym == HttpConstants.LF) {
            break
        } else if (prevSym != HttpConstants.CR) {
            lineBuilder.append(prevSym)
        }
        prevSym = nextSym
        nextSym = inputStream.read().toChar()
    }

    lineBuilder.toString()
}

internal fun readBody(inputStream: InputStream, size: Int): ByteArray {
    val byteArray = ByteArray(size)

    for (i in 0 until size) {
        byteArray[i] = inputStream.read().toByte()
    }
    return byteArray
}

internal suspend fun readChunkedBody(inputStream: InputStream): ByteArray {
    var chunkSize: Int
    val chunks = mutableListOf<ByteArray>()
    do {
        chunkSize = readLine(inputStream).toInt()
        chunks += readBody(inputStream, chunkSize)
        readLine(inputStream) // Empty line
    } while (chunkSize != 0)
    return ByteArrayUtils.merge(chunks)
}
