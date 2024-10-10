package com.grappim.aipal.data.db.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Language(
    @SerialName("id")
    val id: Long,
    @SerialName("name")
    val name: String,
    @SerialName("lngCode")
    val lngCode: String
)
