package uz.notes.model

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(val email:String,val name:String,val surname:String,val password: String)
