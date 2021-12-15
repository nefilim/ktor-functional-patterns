package io.github.nefilim.ktorpatterns.domain

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(val message: String)