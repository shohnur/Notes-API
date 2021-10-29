package uz.notes.db.tables

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import uz.notes.model.User

object UserTable : Table() {
    val email = varchar("email", 512)
    val name = varchar("name", 512)
    val surname = varchar("surname", 512)
    val hashPassword = varchar("hashPassword", 512)

    override val primaryKey: PrimaryKey = PrimaryKey(email)
}


fun rowToUser(row: ResultRow?): User? {
    if (row == null) {
        return null
    }

    return User(
        email = row[UserTable.email],
        hashPassword = row[UserTable.hashPassword],
        name = row[UserTable.name],
        surname = row[UserTable.surname],
    )
}