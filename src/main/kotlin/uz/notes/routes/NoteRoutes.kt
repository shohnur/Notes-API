package uz.notes.routes

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import uz.notes.db.Repository
import uz.notes.model.Note
import uz.notes.model.StatusMessage
import uz.notes.model.User

const val NOTES = "$API_VERSION/notes"
const val CREATE_NOTES = "$NOTES/create"
const val UPDATE_NOTES = "$NOTES/update"
const val DELETE_NOTES = "$NOTES/delete"

fun Route.noteRoutes(db: Repository) {

    authenticate("jwt") {

        post(CREATE_NOTES) {
            val note = try {
                call.receive<Note>()
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, StatusMessage(false, "Missing Some Fields"))
                return@post
            }

            try {
                val email = call.principal<User>()!!.email
                db.addNote(note, email)
                call.respond(HttpStatusCode.OK, StatusMessage(true, "Note Added Successfully"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.Conflict, StatusMessage(false, e.message ?: "Some Problem Occurred"))
            }
        }

        get(NOTES) {
            try {
                val email = call.principal<User>()!!.email
                val notes = db.getUserNotes(email)
                call.respond(HttpStatusCode.OK, notes)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.Conflict, emptyList<Note>())
            }
        }

        delete(DELETE_NOTES) {
            val param = try {
                call.request.queryParameters["id"]!!
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, StatusMessage(false, "Query Parameter is Not Presented"))
                return@delete
            }

            val id = try {
                param.toInt()
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    StatusMessage(false, "Query Parameter's Format is Not Correct")
                )
                return@delete
            }

            try {
                val email = call.principal<User>()!!.email
                val res = db.deleteNote(id, email)
                if (res == 1) call.respond(HttpStatusCode.OK, StatusMessage(true, "Note Deleted Successfully"))
                else throw NotFoundException()
            } catch (e: Exception) {
                call.respond(HttpStatusCode.Conflict, StatusMessage(false, e.message ?: "Some Problem Occurred"))
            }
        }

        put(UPDATE_NOTES) {
            val note = try {
                call.receive<Note>()
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, StatusMessage(false, "Missing Some Fields"))
                return@put
            }

            try {
                val email = call.principal<User>()!!.email
                val res = db.updateNote(note, email)
                if (res == 1) call.respond(HttpStatusCode.OK, StatusMessage(true, "Note Updated Successfully"))
                else throw NotFoundException()
            } catch (e: Exception) {
                call.respond(HttpStatusCode.Conflict, StatusMessage(false, e.message ?: "Some Problem Occured"))
            }
        }

    }

}