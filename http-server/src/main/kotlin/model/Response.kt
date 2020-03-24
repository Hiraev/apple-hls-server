package model

import extensions.mime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import model.constants.HttpConstants
import java.io.File

class Response(
        val code: Int,
        val message: String,
        val httpVersion: String = HttpConstants.HttpVersion.V11,
        headers: Headers = Headers(),
        body: Body = Body.Empty
) : Call(headers, body) {

    companion object {
        fun fromFile(file: File): Response {
            val body =  file.readBytes()
            return Responses.ok.copy(body = Body.ArrayBody(body)).apply {
                file.mime()?.let {
                    headers.add(HttpConstants.Headers.CONTENT_TYPE to it)
                }
            }
        }
    }

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
