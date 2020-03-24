package router

import model.Method
import model.Request
import model.Response

@DslMarker
private annotation class Dsl

class Router {

    private val getRules = mutableListOf<Rule>()
    private val postRules = mutableListOf<Rule>()
    private lateinit var pathNotFound: (suspend (Request) -> Response)
    private lateinit var methodNotFound: (suspend (Request) -> Response)

    @Dsl
    fun POST(matcher: Matcher, action: suspend (Request) -> Response) {
        postRules += Rule(matcher, action)
    }

    @Dsl
    fun GET(matcher: Matcher, action: suspend (Request) -> Response) {
        getRules += Rule(matcher, action)
    }

    fun setPathNotFoundAction(action: suspend (Request) -> Response) {
        pathNotFound = action
    }

    fun setMethodNotAllowedAction(action: suspend (Request) -> Response) {
        methodNotFound = action
    }

    suspend fun handle(request: Request): Response {
        val action = when (request.method) {
            Method.GET -> tryPath(request.uri.path, getRules)
            Method.POST -> tryPath(request.uri.path, postRules)
            Method.OTHER -> methodNotFound
        }
        return action.invoke(request)
    }

    private fun tryPath(path: String, rules: List<Rule>) = rules.find { it.matcher.match(path) }?.action
            ?: pathNotFound

    private data class Rule(
            val matcher: Matcher,
            val action: suspend (Request) -> Response
    )

}

