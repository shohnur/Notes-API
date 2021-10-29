package uz.notes.model

import kotlinx.serialization.Serializable

@Serializable
data class TokenMessage(val success: Boolean, val message: String, val access_token: String)