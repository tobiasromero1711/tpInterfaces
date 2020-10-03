package org.example

import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.*
import io.javalin.core.util.RouteOverviewPlugin
import org.unq.ui.bootstrap.getInstagramSystem


fun main() {

    // setUp controllers
    val instagramSystem =  getInstagramSystem()
    val userController = UserController(instagramSystem)

    // Configuracion
    val app = Javalin.create {
            it.defaultContentType = "application/json"
            it.registerPlugin(RouteOverviewPlugin("/routes"))
                    /*it.accessManager()*/
    }

    app.start(7000)

    app.routes {
        path("/"){
            get(userController::getUsers)
        }
        path("register"){
            post(userController::register)

        }
    }

}

