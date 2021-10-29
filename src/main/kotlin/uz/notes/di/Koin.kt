package uz.notes.di

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.koin.dsl.module
import uz.notes.auth.JWTService
import uz.notes.db.DatabaseFactory
import uz.notes.db.Repository
import java.net.URI
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

val authModule = module {

    fun provideHashKey() = System.getenv("HASH_KEY").toByteArray()

    fun provideHmacKey(hashKey: ByteArray) = SecretKeySpec(hashKey, "HmacSHA1")

    val issuer = "noteServer"
    val algorithm = Algorithm.HMAC512(System.getenv("JWT_SECRET"))

    fun provideVerifier() = JWT
        .require(algorithm)
        .withIssuer(issuer)
        .build()

    fun provideHmac(hmacKeySpec: SecretKeySpec) = Mac.getInstance("HmacSHA1").apply {
        init(hmacKeySpec)
    }

    single { provideHashKey() }
    single { provideHmacKey(get()) }
    single { provideHmac(get()) }
    single { provideVerifier() }
    single { issuer }
    single { algorithm }
    single { JWTService() }
}

val databaseModule = module {

    fun provideHikariDataSource(hikariConfig: HikariConfig) = HikariDataSource(hikariConfig)

    fun provideHikariConfig() = HikariConfig().apply {
        val dbUri = URI(System.getenv("DATABASE_URL"))
//        val username = dbUri.userInfo.split(":").toTypedArray()[0]
//        val password = dbUri.userInfo.split(":").toTypedArray()[1]

        driverClassName = System.getenv("JDBC_DRIVER")
        maximumPoolSize = 3
        jdbcUrl = dbUri.toString()
//            "jdbc:postgresql://" + dbUri.host + ":" + dbUri.port + dbUri.path + "?sslmode=require" + "&user=$username&password=$password"
        isAutoCommit = false
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        validate()
    }

    single { provideHikariDataSource(get()) }
    single { provideHikariConfig() }

    single { DatabaseFactory(get()) }
    single { Repository(get()) }
}