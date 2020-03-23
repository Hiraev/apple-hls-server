package model.constants

object HttpConstants {

    const val CR = '\r'
    const val LF = '\n'
    const val CRLF = "$CR$LF"
    const val EOF = (-1).toChar()

    object Headers {
        const val CONTENT_TYPE = "content-type"
        const val CONTENT_LENGTH = "content-length"
        const val TRANSFER_ENCODING = "transfer-encoding"
        const val CONNECTION = "connection"
        const val LOCATION = "Location"

        const val TRANSFER_ENCODING_CHUNKED = "chunked"
        const val CONNECTION_CLOSE = "close"
    }

    object HttpVersion {

        const val V11 = "HTTP/1.1"

    }

}
