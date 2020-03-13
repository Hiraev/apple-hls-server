package extensions

import model.Headers

fun Headers.isChunked() = get(Headers.TRANSFER_ENCODING)?.contains(Headers.TRANSFER_ENCODING_CHUNKED) == true

fun Headers.contentLength() = get(Headers.CONTENT_LENGTH)?.toIntOrNull()

fun Headers.isKeepAlive() = !get(Headers.CONNECTION).equals(Headers.CONNECTION_CLOSE)

fun Headers.getBoundary() =
    get(Headers.CONTENT_TYPE)
        ?.split(";")?.map(String::trim)?.find { it.startsWith("boundary") }
        ?.split("=")?.map(String::trim)?.get(1)
