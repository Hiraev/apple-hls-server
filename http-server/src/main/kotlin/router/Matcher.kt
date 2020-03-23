package router

sealed class Matcher {

    class PrefixMatcher(val prefix: String) : Matcher()
    class FullMatcher(val pattern: String) : Matcher()
    object NoMatcher : Matcher()

    fun match(string: String) = when (this) {
        is PrefixMatcher -> string.startsWith(prefix)
        is FullMatcher -> string == pattern
        is NoMatcher -> true
    }

}

fun String.fullMatcher() = Matcher.FullMatcher(this)

fun String.prefixMatcher() = Matcher.PrefixMatcher(this)
