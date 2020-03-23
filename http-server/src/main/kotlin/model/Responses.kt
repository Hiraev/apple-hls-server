package model

import model.constants.HttpCodeAndMessage
import model.constants.HttpConstants

object Responses {

    val ok = HttpCodeAndMessage.OK.toResponse()
    val notFound = HttpCodeAndMessage.NOT_FOUND.toResponse()
    val methodNotAllowed = HttpCodeAndMessage.METHOD_NOT_ALLOWED.toResponse()

    fun redirectTo(path: String) = HttpCodeAndMessage.REDIRECT.toResponse().apply {
        headers.add(HttpConstants.Headers.LOCATION to path)
    }

}
