package uz.notes.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.util.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import uz.notes.model.User
import javax.crypto.Mac


class JWTService : KoinComponent {

    private val issuer: String by inject()
    private val algorithm: Algorithm by inject()
    private val hmac: Mac by inject()
    val verifier: JWTVerifier by inject()

    fun generateToken(user: User) =
        JWT.create()
            .withSubject("NoteAuthentication")
            .withIssuer(issuer)
            .withClaim("email", user.email)
            .sign(algorithm)

    fun hash(password: String): String {
        return hex(hmac.doFinal(password.toByteArray(Charsets.UTF_8)))
    }
}
