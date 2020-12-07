package tech.gamedev.storemanager.data.notification

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import tech.gamedev.storemanager.data.constants.Constants.BASE_URL

class RetrofitInstance {

    companion object{

        private val retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        val api by lazy {
            retrofit.create(NotificationAPI::class.java)
        }
    }
}