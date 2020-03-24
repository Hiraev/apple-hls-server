package model

import model.constants.HttpConstants

class Headers {

    private val headers = mutableMapOf<String, String>()

    fun add(nameAndValue: Pair<String, String>) {
        add(nameAndValue.first.toLowerCase(), nameAndValue.second.toLowerCase())
    }

    fun get(name: String) = headers[name]

    fun isNotEmpty() = headers.isNotEmpty()

    override fun toString() = headers.map { it.key + ": " + it.value + HttpConstants.CRLF }.reduce(String::plus)

    private fun add(name: String, value: String) {
        headers += name to value
    }

}
