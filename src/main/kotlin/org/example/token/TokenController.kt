package org.example.token


import com.auth0.jwt.JWT
import com.auth0.jwt.JWTCreator
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import javalinjwt.JWTGenerator
import javalinjwt.JWTProvider
import java.lang.NullPointerException
import kotlin.Exception
import org.unq.ui.model.User

class NotValidToken : Exception("Not valid token")

class TokenController {

    val algorithm: Algorithm = Algorithm.HMAC256("very_secret")

    val generator: JWTGenerator<User> = JWTGenerator<User> { user: User, alg: Algorithm? ->
        val token: JWTCreator.Builder = JWT.create()
                .withClaim("id", user.id)
        token.sign(alg)
    }

    val verifier: JWTVerifier = JWT.require(algorithm).build()

    val provider = JWTProvider(algorithm, generator, verifier)

    fun genereteToken(user: User): String {
        return provider.generateToken(user)
    }

    fun validateToken(token: String): String {
        val decodedJWT =  provider.validateToken(token)
        if (decodedJWT.isPresent() && decodedJWT.get().claims.contains("id")) {
            return decodedJWT.get().getClaim("id").asString()
        }
        throw NotValidToken()
    }

}