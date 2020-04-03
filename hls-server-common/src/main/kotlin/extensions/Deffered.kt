package extensions

import kotlinx.coroutines.Deferred

fun <T> Deferred<T>.completedOrNull(): T? = if (isCompleted) getCompleted() else null