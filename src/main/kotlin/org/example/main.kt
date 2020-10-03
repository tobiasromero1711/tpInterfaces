package org.example

import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.*
import io.javalin.core.security.Role
import io.javalin.core.util.RouteOverviewPlugin
import org.unq.ui.bootstrap.getInstagramSystem


fun main() = InstagramApi().start()

enum class InstagramApiRoles: Role {
    ANYONE, USER
}

class InstagramApi(){

    // setUp controllers
    val instagramSystem =  getInstagramSystem()
    val userController = UserController(instagramSystem)

    // Configuracion
    val app = Javalin.create {
            it.defaultContentType = "application/json"
            it.registerPlugin(RouteOverviewPlugin("/routes"))
            it.accessManager(InstagramApiAccessManager(instagramSystem))
    }

    fun start() {

        app.start(8080)

        app.routes {
            /*path("/") {
                get(userController::getUsers, setOf(InstagramApiRoles.ANYONE))
            }
            */
            path("register")
            {
                post(userController::register, setOf(InstagramApiRoles.ANYONE))
            }
            path("login")
            {
                post(userController::login, setOf(InstagramApiRoles.ANYONE))
            }

        }
    }
}

