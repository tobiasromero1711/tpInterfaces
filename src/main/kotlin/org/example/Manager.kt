package org.example

import io.javalin.core.security.AccessManager
import io.javalin.core.security.Role
import io.javalin.http.Context
import io.javalin.http.Handler
import io.javalin.http.UnauthorizedResponse
import org.unq.ui.model.NotFound
import org.unq.ui.model.InstagramSystem
import org.example.token.NotValidToken
import org.example.token.TokenController

class InstagramApiAccessManager(val instagramSystem: InstagramSystem) : AccessManager {

    val tokenController = TokenController()

    override fun manage(handler: Handler, ctx: Context, roles: MutableSet<Role>) {
        val token = ctx.header("Authorization")
        when {
            roles.contains(InstagramApiRoles.ANYONE) -> handler.handle(ctx)
            token === null -> throw UnauthorizedResponse()
            roles.contains(InstagramApiRoles.USER) -> {
                try {
                    val userId = tokenController.validateToken(token)
                    instagramSystem.getUser(userId)
                    ctx.attribute("userId", userId)
                    handler.handle(ctx)
                } catch (e: NotValidToken) {
                    throw UnauthorizedResponse("Not valid Token")
                } catch (e: NotFound) {
                    throw UnauthorizedResponse("Not valid Token")
                }
            }
        }
    }
}