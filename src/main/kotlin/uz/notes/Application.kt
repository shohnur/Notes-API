package uz.notes

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.features.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.netty.*
import org.koin.core.context.startKoin
import org.koin.ktor.ext.inject
import uz.notes.auth.JWTService
import uz.notes.db.DatabaseFactory
import uz.notes.db.Repository
import uz.notes.di.authModule
import uz.notes.di.databaseModule
import uz.notes.model.User
import uz.notes.routes.noteRoutes
import uz.notes.routes.userRoutes

fun main(args: Array<String>): Unit =
    EngineMain.main(args)


@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {

    startKoin {
        modules(listOf(databaseModule, authModule))
    }
    val dbFactory: DatabaseFactory by inject()
    dbFactory.init()
    val db: Repository by inject()
    val jwtService: JWTService by inject()
    val hashFunction = { pass: String -> jwtService.hash(pass) }

    install(Authentication) {
        jwt("jwt") {
            verifier(jwtService.verifier)
            realm = "Note Server"
            validate {
                val payload = it.payload
                val email = payload.getClaim("email").asString()
                val user = db.findUserByEmail(email)
                user
            }
        }
    }

    install(ContentNegotiation) {
        json()
    }


    routing {
        userRoutes(db, jwtService, hashFunction)
        noteRoutes(db)
    }
}


