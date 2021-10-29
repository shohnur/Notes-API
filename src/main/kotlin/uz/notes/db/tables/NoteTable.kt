package uz.notes.db.tables

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import uz.notes.model.Note

object NoteTable : Table() {

    val id = integer("id").autoIncrement()
    val userEmail = varchar("userEmail", 512).references(UserTable.email)
    val title = varchar("title", 512)
    val note = varchar("note", 512)
    val date = varchar("date", 512)

    override val primaryKey: PrimaryKey = PrimaryKey(id)

}

fun rowToNote(row: ResultRow?): Note? {
    if (row == null) return null

    return Note(
        id = row[NoteTable.id],
        title = row[NoteTable.title],
        note = row[NoteTable.note],
        date = row[NoteTable.date]
    )
}