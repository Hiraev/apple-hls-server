package model.constants

import model.Response

enum class HttpCodeAndMessage(val code: Int, val message: String) {

    OK(200, "OK"),
    NOT_FOUND(404, "Not Found"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
    REDIRECT(301, "Redirect");

    fun toResponse() = Response(code, message)

}
