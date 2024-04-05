package io.cursedfunction.models

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class BlogPost(
    val id: Int,
    val title: String,
    val content: String,
    val timestamp: Instant
)