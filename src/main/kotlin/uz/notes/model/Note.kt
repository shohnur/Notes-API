package uz.notes.model

import kotlinx.serialization.Serializable

@Serializable
data class Note(val id: Int, val title: String, val note: String, val date: String)
