package io.github.nefilim.ktorpatterns.domain

import java.time.LocalDateTime

typealias TimeProvider = () -> LocalDateTime

val defaultTimeProvider: TimeProvider = { LocalDateTime.now() }