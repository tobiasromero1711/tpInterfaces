package org.example

import io.javalin.http.BadRequestResponse
import io.javalin.http.Context
import org.unq.ui.model.InstagramSystem
import org.unq.ui.model.UsedEmail

class UserController(private val instagramSystem: InstagramSystem) {

     fun register(ctx: Context){
        val userRegisterDTO = ctx.body<UserRegisterDTO>()
        try {
            instagramSystem.register(userRegisterDTO.name,userRegisterDTO.password,userRegisterDTO.email,userRegisterDTO.image)
            // TODO : AGREGAR TOKEN

            ctx.json(ResponseOK())
            }
        catch (e: UsedEmail){
            throw BadRequestResponse(e.message!!) // TODO: revisar esto
        }

    }

    fun getUsers(ctx: Context){
        val usersDTO = instagramSystem.users.map { UserDTO(it.name,it.email) }
        ctx.json(usersDTO)
    }
}

data class UserRegisterDTO(val name: String, val email: String, val password: String,val image: String)
data class UserDTO(val name:String,val email: String)
data class ResponseOK(val result: String = "ok")