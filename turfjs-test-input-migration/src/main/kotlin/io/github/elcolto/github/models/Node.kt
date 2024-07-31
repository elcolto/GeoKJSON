package io.github.elcolto.github.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class Node(
    @SerialName("mode")
    val mode: String,
    @SerialName("path")
    val path: String,
    @SerialName("sha")
    val sha: String,
    @SerialName("size")
    val size: Int? = null,
    @SerialName("type")
    val type: String,
    @SerialName("url")
    val url: String
)
