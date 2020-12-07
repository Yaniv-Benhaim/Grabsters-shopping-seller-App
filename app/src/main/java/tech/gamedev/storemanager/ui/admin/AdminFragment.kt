package tech.gamedev.storemanager.ui.admin

import tech.gamedev.storemanager.data.notification.NotificationData
import tech.gamedev.storemanager.data.notification.PushNotification
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_admin.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tech.gamedev.storemanager.R
import tech.gamedev.storemanager.data.notification.RetrofitInstance
import java.lang.Exception

const val TOPIC = "/topics/myTopic"


class AdminFragment : Fragment(R.layout.fragment_admin) {

    val TAG = "AdminFragment"


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)

        btnSendPushNotification.setOnClickListener {

            val title = etPushTitle.text.toString()
            val message = etPushMessage.text.toString()
            val token = etPushToken.text.toString()

            if(title.isNotEmpty() && message.isNotEmpty()){
                PushNotification(
                     NotificationData(title,message),
                      TOPIC
                ).also {
                    sendNotification(it)
                }
            }
        }
    }

    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
            if(response.isSuccessful) {
                Log.d(TAG, "Response: ${Gson().toJson(response)}")
            } else {
                Log.e(TAG, response.errorBody().toString())
            }
        } catch(e: Exception) {
            Log.e(TAG, e.toString())
        }

    }

}