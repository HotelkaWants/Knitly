package com.hotelka.knitlyWants.FirebaseUtils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.hotelka.knitlyWants.MainActivity
import com.hotelka.knitlyWants.R

class FirebaseMessageReceiver : FirebaseMessagingService() {

    // Override onNewToken to get new token
    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    // Override onMessageReceived() method to extract the
    // title and
    // body from the message passed in FCM
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // First case when notifications are received via
        // data event
        // Here, 'title' and 'message' are the assumed names
        // of JSON
        // attributes. Since here we do not have any data
        // payload, This section is commented out. It is
        // here only for reference purposes.
        /*if(remoteMessage.getData().size()>0){
            showNotification(remoteMessage.getData().get("title"),
                          remoteMessage.getData().get("message"));
        }*/

        // Second case when notification payload is
        // received.
        if (remoteMessage.getNotification() != null) {
            // Since the notification is received directly from
            // FCM, the title and the body can be fetched
            // directly as below.
            showNotification(
                remoteMessage.getNotification()?.title.toString(),
                remoteMessage.getNotification()?.body.toString()
            )
        }
    }

    private fun getCustomDesign(
        title: String,
        message: String
    ): RemoteViews {
        val remoteViews = RemoteViews(
           baseContext.packageName,
            R.layout.comment_notification
        )
        remoteViews.setTextViewText(R.id.username, title)
        remoteViews.setTextViewText(R.id.message, message)

        return remoteViews
    }

    // Method to display the notifications
    fun showNotification(
        title: String,
        message: String
    ) {
        // Pass the intent to switch to the MainActivity
        val intent = Intent(this, MainActivity::class.java)
        // Assign channel ID
        val channel_id = "notification_channel"
        // Here FLAG_ACTIVITY_CLEAR_TOP flag is set to clear
        // the activities present in the activity stack,
        // on the top of the Activity that is to be launched
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        // Pass the intent to PendingIntent to start the
        // next Activity
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        // Create a Builder object using NotificationCompat
        // class. This will allow control over all the flags
        var builder: NotificationCompat.Builder = NotificationCompat.Builder(
            baseContext,
            channel_id
        )
            .setSmallIcon(R.mipmap.ic_launcher)
            .setAutoCancel(true)
            .setVibrate(
                longArrayOf(
                    1000, 1000, 1000,
                    1000, 1000
                )
            )
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)

        // A customized design for the notification can be
        // set only for Android versions 4.1 and above. Thus
        // condition for the same is checked here.
        builder = builder.setContent(
            getCustomDesign(title, message)
        )
        // Create an object of NotificationManager class to
        // notify the
        // user of events that happen in the background.
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager?
        // Check if the Android Version is greater than Oreo
        val notificationChannel = NotificationChannel(
            channel_id, "web_app",
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager!!.createNotificationChannel(
            notificationChannel
        )
        notificationManager.notify(0, builder.build())
    }
}