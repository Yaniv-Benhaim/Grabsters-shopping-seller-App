package tech.gamedev.storemanager.data.models

class User(
    private val email: String,
    private val id: String,
    private val imgUrl: String,
    private val name: String
) {

    fun getEmail(): String{
        return email
    }

    fun getId(): String{
        return id
    }

    fun getImgUrl(): String{
        return imgUrl
    }

    fun getName(): String{
        return name
    }
}