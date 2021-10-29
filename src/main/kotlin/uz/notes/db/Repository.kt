package uz.notes.db

import org.jetbrains.exposed.sql.*
import org.koin.core.component.KoinComponent
import uz.notes.db.tables.NoteTable
import uz.notes.db.tables.UserTable
import uz.notes.db.tables.rowToNote
import uz.notes.db.tables.rowToUser
import uz.notes.model.Note
import uz.notes.model.User

class Repository(private val db: DatabaseFactory) : KoinComponent {

    /**user*/

    suspend fun registerUser(user: User) =
        db.dbQuery {
            UserTable.insert { ut ->
                ut[email] = user.email
                ut[name] = user.name
                ut[surname] = user.surname
                ut[hashPassword] = user.hashPassword
            }
        }


    suspend fun findUserByEmail(email: String) = db.dbQuery {
        UserTable.select { UserTable.email.eq(email) }
            .map { rowToUser(it) }
            .singleOrNull()
    }


    /**notes*/

    suspend fun addNote(newNote: Note, email: String) =
        db.dbQuery {
            NoteTable.insert { nt ->
                nt[note] = newNote.note
                nt[title] = newNote.title
                nt[userEmail] = email
                nt[date] = newNote.date
            }
        }

    suspend fun getUserNotes(email: String) =
        db.dbQuery {
            NoteTable.select {
                NoteTable.userEmail.eq(email)
            }.mapNotNull {
                rowToNote(it)
            }.sortedBy { it.id }
        }

    suspend fun deleteNote(id: Int, email: String) =
        db.dbQuery {
            NoteTable.deleteWhere { NoteTable.id.eq(id) and NoteTable.userEmail.eq(email) }
        }

    suspend fun updateNote(newNote: Note, email: String) =
        db.dbQuery {
            NoteTable.update({
                NoteTable.id.eq(newNote.id) and NoteTable.userEmail.eq(email)
            }) {
                it[note] = newNote.note
                it[title] = newNote.title
                it[date] = newNote.date
            }
        }
}