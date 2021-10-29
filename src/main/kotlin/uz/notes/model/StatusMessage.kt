package uz.notes.model

import kotlinx.serialization.Serializable

@Serializable
data class StatusMessage(val success: Boolean, val message: String)