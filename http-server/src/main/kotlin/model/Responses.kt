package model

import model.constants.HttpCodeAndMessage
import model.constants.HttpConstants

object Responses {

    fun ok() = HttpCodeAndMessage.OK.toResponse()
    fun notFound() = HttpCodeAndMessage.NOT_FOUND.toResponse()
    fun methodNotAllowed() = HttpCodeAndMessage.METHOD_NOT_ALLOWED.toResponse()

    fun redirectTo(path: String) = HttpCodeAndMessage.REDIRECT.toResponse().apply {
        headers.add(HttpConstants.Headers.LOCATION to path)
    }

}
