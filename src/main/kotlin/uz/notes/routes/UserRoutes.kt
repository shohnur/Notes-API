package uz.notes.routes

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import uz.notes.auth.JWTService
import uz.notes.db.Repository
import uz.notes.model.*

const val API_VERSION = "/v1"
const val USERS = "$API_VERSION/users"
const val REGISTER_REQUEST = "$USERS/register"
const val LOGIN_REQUEST = "$USERS/login"

fun Route.userRoutes(db: Repository, jwtService: JWTService, hashFunction: (String) -> String) {

    post(REGISTER_REQUEST) {

        val registerRequest = try {
            call.receive<RegisterRequest>()
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest, StatusMessage(false, "Missing Some Fields"))
            return@post
        }

        try {
            val user = User(
                registerRequest.email,
                registerRequest.name,
                registerRequest.surname,
                hashFunction(registerRequest.password)
            )
            db.registerUser(user)
            call.respond(HttpStatusCode.OK, StatusMessage(true, "User Registered Successfully"))
        } catch (e: Exception) {
            call.respond(HttpStatusCode.Conflict, StatusMessage(false, e.message ?: "Some Problem Occurred"))
        }

    }

    post(LOGIN_REQUEST) {
        val loginRequest = try {
            call.receive<LoginRequest>()
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest, StatusMessage(false, e.message ?: "Missing Some Fields"))
            return@post
        }

        try {
            val user = db.findUserByEmail(loginRequest.email)
            if (user == null) {
                call.respond(HttpStatusCode.NotFound, StatusMessage(false, "User Not Found"))
            } else {
                if (user.hashPassword == hashFunction(loginRequest.password)) {
                    call.respond(
                        HttpStatusCode.OK,
                        TokenMessage(true, "Token generated", jwtService.generateToken(user))
                    )
                } else {
                    call.respond(HttpStatusCode.BadRequest, StatusMessage(false, "Password Incorrect"))
                }
            }
        } catch (e: Exception) {
            call.respond(HttpStatusCode.Conflict, StatusMessage(false, e.message ?: "Some Problem Occurred"))
        }
    }

}