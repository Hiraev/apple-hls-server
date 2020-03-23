package model

import model.constants.HttpConstants

class Response(
        val code: Int,
        val message: String,
        val httpVersion: String = HttpConstants.HttpVersion.V11,
        headers: Headers = Headers.EMPTY,
        body: Body = Body.Empty
) : Call(headers, body) {

    init {
        if (body.size != 0) {
            headers.add(HttpConstants.Headers.CONTENT_LENGTH to body.size.toString())
        }
    }

    fun copy(
            code: Int = this.code,
            message: String = this.message,
            httpVersion: String = this.httpVersion,
            headers: Headers = this.headers,
            body: Body = this.body
    ) = Response(code, message, httpVersion, headers, body)

}
