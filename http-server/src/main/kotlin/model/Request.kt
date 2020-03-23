package model

import java.net.URI

class Request(
        val method: Method,
        val uri: URI,
        headers: Headers,
        body: Body
) : Call(headers, body)
