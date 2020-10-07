package org.example

import io.javalin.http.BadRequestResponse
import io.javalin.http.Context
import org.example.token.TokenController
import org.unq.ui.model.InstagramSystem
import org.unq.ui.model.NotFound
import org.unq.ui.model.UsedEmail

class UserController(private val instagramSystem: InstagramSystem) {

    val tokenController = TokenController()

    private fun getUserId(ctx: Context): String {
        return ctx.attribute<String>("userId") ?: throw BadRequestResponse("Not found user")
    }

    fun getUser(ctx: Context){
        val userId = getUserId(ctx)

        // TODO: DEBERIAMOS VERIFICAR QUE EXISTE EL USUARIO??

        val user = instagramSystem.getUser(userId)
        val timeline = instagramSystem.timeline(userId)
        // TODO : TENDRIAMOS QUE MAPEAR TODO ACA ??
        val followers = user.followers.map { BasicUserDTO(user.name,user.image) }
        val userPosts = timeline.map {
            val usersDTO= it.likes.map { BasicUserDTO(it.name,it.image) }
            val userDTO = BasicUserDTO(it.user.name,it.user.image)
            PostDTO(it.id,it.description,it.portrait,it.landscape,usersDTO,it.date.toString(),userDTO) }

        val userDTO = UserDTO(user.name,user.image, followers,userPosts)

        ctx.status(200).json(userDTO)
    }

     fun register(ctx: Context){
        val userRegisterDTO = ctx.body<UserRegisterDTO>()
        try {
            val user = instagramSystem.register(userRegisterDTO.name,userRegisterDTO.email,userRegisterDTO.password,userRegisterDTO.image)
            ctx.header("Authorization", tokenController.generateToken(user))
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
            ctx.header("Authorization", tokenController.generateToken(user))
            ctx.status(200).json(ResponseOK())

        }
        catch (e:NotFound){
            ctx.status(404).json(ResponseError(e.message!!))
        }
    }



//    fun getUsers(ctx: Context){
//        val usersDTO = instagramSystem.users.map { UserDTO(it.name,it.email) }
//        ctx.json(usersDTO)
//    }
}


// DTOs
data class UserLoginDTO(val email:String,val password: String)
data class UserRegisterDTO(val name: String, val email: String, val password: String,val image: String)
data class UserDTO(val name:String, val image: String, val followers: List<BasicUserDTO>, val timeline: List<PostDTO>)

data class PostDTO(
    val id:String,
    val description:String,
    val portrait:String,
    val landscape:String,
    val likes: List<BasicUserDTO>,
    val date: String,
    val user: BasicUserDTO
    )

data class BasicUserDTO(val name: String, val image: String)


// responses DTO
data class ResponseOK(val result: String = "ok")
data class ResponseError(val message: String,val result: String = "error")