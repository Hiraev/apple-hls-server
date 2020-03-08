package model

private const val CR = '\r'
private const val LF = '\n'

class Headers {

    companion object {
        const val CONTENT_TYPE = "Content-Type"
        const val CONTENT_LENGTH = "Content-Length"
        const val TRANSFER_ENCODING = "Transfer-Encoding"

        const val TRANSFER_ENCODING_CHUNKED = "chunked"
        const val CONTENT_TYPE_MP4 = "video/mp4"
        const val CONTENT_TYPE_TS = "video/mp2t"
        const val CONTENT_TYPE_M3U8 = "application/vnd.apple.mpegur"
    }

    private val headers = mutableMapOf<String, String>()

    fun add(nameAndValue: Pair<String, String>) {
        add(nameAndValue.first, nameAndValue.second)
    }

    fun get(name: String) = headers[name]

    private fun add(name: String, value: String) {
        headers += name to value
    }

    fun isNotEmpty() = headers.isNotEmpty()

    override fun toString() = headers.map { it.key + ": " + it.value + CR + LF }.reduce(String::plus)

}
