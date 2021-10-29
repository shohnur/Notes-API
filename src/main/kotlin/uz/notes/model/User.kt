package uz.notes.model

import io.ktor.auth.*
import kotlinx.serialization.Serializable

@Serializable
data class User(val email: String, val name: String, val surname: String, val hashPassword: String) : Principal
