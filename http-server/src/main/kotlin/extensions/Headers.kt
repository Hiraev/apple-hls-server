package extensions

import model.Headers
import model.constants.HttpConstants

fun Headers.isChunked() =
        get(HttpConstants.Headers.TRANSFER_ENCODING)?.contains(HttpConstants.Headers.TRANSFER_ENCODING_CHUNKED) == true

fun Headers.contentLength() = get(HttpConstants.Headers.CONTENT_LENGTH)?.toIntOrNull()

fun Headers.isKeepAlive() = !get(HttpConstants.Headers.CONNECTION).equals(HttpConstants.Headers.CONNECTION_CLOSE)

fun Headers.getBoundary() =
        get(HttpConstants.Headers.CONTENT_TYPE)
                ?.split(";")?.map(String::trim)?.find { it.startsWith("boundary") }
                ?.split("=")?.map(String::trim)?.get(1)
