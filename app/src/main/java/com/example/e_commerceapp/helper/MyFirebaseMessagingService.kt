package com.example.e_commerceapp.helper

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.e_commerceapp.R
import com.example.e_commerceapp.ui.activity.ShoppingActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val TAG = "FirebaseMessagingService"

    override fun onNewToken(token: String) {
        Log.d("New_Token", token)
    }

    @SuppressLint(/* ...value = */ "LongLogTag")

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "Poruka: ${remoteMessage.from}")

        if (remoteMessage.notification != null) {
            Log.d(TAG, "Sadr: ${remoteMessage.notification?.body}")
            sendNotification(remoteMessage)
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun sendNotification(remoteMessage: RemoteMessage) {
        val intent = Intent(this, ShoppingActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(
            this@MyFirebaseMessagingService,
            0,
            intent,
            PendingIntent.FLAG_ONE_SHOT
        )
        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder =
            NotificationCompat.Builder(this@MyFirebaseMessagingService, "Notification")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Poruka")
                .setContentText(remoteMessage.notification?.body)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, notificationBuilder.build())
    }
}