package io.github.nefilim.ktorpatterns.service

tailrec fun anyCause(t: Throwable?, predicate: (Throwable) -> Boolean): Boolean {
    return when {
        t == null -> false
        predicate(t) -> true
        else -> anyCause(t.cause, predicate)
    }
}