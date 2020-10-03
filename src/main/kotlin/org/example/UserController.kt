package org.example

import io.javalin.http.BadRequestResponse
import io.javalin.http.Context
import org.example.token.TokenController
import org.unq.ui.model.InstagramSystem
import org.unq.ui.model.NotFound
import org.unq.ui.model.UsedEmail

class UserController(private val instagramSystem: InstagramSystem) {

    val tokenController = TokenController()

     fun register(ctx: Context){
        val userRegisterDTO = ctx.body<UserRegisterDTO>()
        try {
            val user = instagramSystem.register(userRegisterDTO.name,userRegisterDTO.email,userRegisterDTO.password,userRegisterDTO.image)
            ctx.header("Authorization", tokenController.genereteToken(user))
            ctx.status(201).json(ResponseOK())
            }
        catch (e: UsedEmail){
            throw BadRequestResponse(e.message!!) // TODO: revisar esto
        }

    }

    fun login(ctx: Context){
        val userLoginDTO = ctx.body<UserLoginDTO>()
        try {
            val user = instagramSystem.login(userLoginDTO.email,userLoginDTO.password)
            ctx.header("Authorization", tokenController.genereteToken(user))
            ctx.status(200).json(ResponseOK())

        }
        catch (e:NotFound){
            ctx.status(404).json(ResponseError(e.message!!))
        }
    }

    fun getUsers(ctx: Context){
        val usersDTO = instagramSystem.users.map { UserDTO(it.name,it.email) }
        ctx.json(usersDTO)
    }
}

data class UserLoginDTO(val email:String,val password: String)
data class UserRegisterDTO(val name: String, val email: String, val password: String,val image: String)
data class UserDTO(val name:String,val email: String)
data class ResponseOK(val result: String = "ok")
data class ResponseError(val message: String,val result: String = "error")