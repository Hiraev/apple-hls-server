package model

private const val CR = '\r'
private const val LF = '\n'

class Headers {

    companion object {
        const val CONTENT_TYPE = "content-type"
        const val CONTENT_LENGTH = "content-length"
        const val TRANSFER_ENCODING = "transfer-encoding"
        const val CONNECTION = "connection"

        const val TRANSFER_ENCODING_CHUNKED = "chunked"
        const val CONTENT_TYPE_MP4 = "video/mp4"
        const val CONTENT_TYPE_HTML = "text/html"
        const val CONTENT_TYPE_CSS = "text/css"
        const val CONTENT_TYPE_TS = "video/mp2t"
        const val CONTENT_TYPE_M3U8 = "application/vnd.apple.mpegur"
        const val CONNECTION_CLOSE = "close"
    }

    private val headers = mutableMapOf<String, String>()

    fun add(nameAndValue: Pair<String, String>) {
        add(nameAndValue.first.toLowerCase(), nameAndValue.second.toLowerCase())
    }

    fun get(name: String) = headers[name]

    private fun add(name: String, value: String) {
        headers += name to value
    }

    fun isNotEmpty() = headers.isNotEmpty()

    fun isKeepAlive() = !headers.containsKey(CONNECTION) || !headers[CONNECTION].equals(CONNECTION_CLOSE)

    fun getBoudary() =
        headers[CONTENT_TYPE]
            ?.split(";")?.map(String::trim)?.find { it.startsWith("boundary") }
            ?.split("=")?.map(String::trim)?.get(1)

    override fun toString() = headers.map { it.key + ": " + it.value + CR + LF }.reduce(String::plus)

}
