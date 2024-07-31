package io.github.elcolto.github.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class GitHubTree(
    @SerialName("sha")
    val sha: String,
    @SerialName("tree")
    val tree: List<Node> = emptyList(),
    @SerialName("truncated")
    val truncated: Boolean = false,
    @SerialName("url")
    val url: String
)
