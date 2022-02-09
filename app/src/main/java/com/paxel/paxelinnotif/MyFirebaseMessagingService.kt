package com.paxel.paxelinnotif

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

/**
 * Created by Siva G Gurusamy on 04,Feb,2022
 * email : siva@paxel.co
 */

const val channelId = "001"
const val channelName = "High Priority Notification"
@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class MyFirebaseMessagingService : FirebaseMessagingService() {
    // generate notification
    // mount the created notif with custom layout
    // show notif

    override fun onMessageReceived(remoteMsg: RemoteMessage) {
        removeBrokenChannel()
        if(remoteMsg.notification != null) {
            generateNotif(remoteMsg.notification!!.title!!,remoteMsg.notification!!.body!!)
        }
    }

    private fun notifChannelCreation() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channelName = getString(R.string.high_channel_title)
        val channelDescription = getString(R.string.high_channel_description)
        val importance = NotificationManagerCompat.IMPORTANCE_HIGH
        val channel = NotificationChannelCompat.Builder(CHANNEL_ID, importance).apply {
            setName(channelName)
            setDescription(channelDescription)
            setSound(
                Uri.parse("${ContentResolver.SCHEME_ANDROID_RESOURCE}://${this@MyFirebaseMessagingService.packageName}/raw/pxl_sound"),
                Notification.AUDIO_ATTRIBUTES_DEFAULT
            )
        }
        NotificationManagerCompat.from(applicationContext).createNotificationChannel(channel.build())
    }

    @SuppressLint("RemoteViewLayout")
    fun getRemoteView(title: String, msg: String): RemoteViews {
        val remoteView = RemoteViews("com.paxel.paxelinnotif", R.layout.custom_notification)

        remoteView.setTextViewText(R.id.title,title)
        remoteView.setTextViewText(R.id.desc,msg)
        remoteView.setImageViewResource(R.id.app_logo, R.drawable.ic_launcher_foreground)
         return remoteView
    }

    fun generateNotif(title: String, msg: String) {

        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT)

        //channel id, and it's name
        var builder: NotificationCompat.Builder = NotificationCompat.Builder(applicationContext,
            channelId)
            .setSmallIcon(R.id.app_logo)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(1000,1000,1000,1000))
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setSound(Uri.parse("${ContentResolver.SCHEME_ANDROID_RESOURCE}://${this.packageName}/${R.raw.pxl_sound}"))
        builder = builder.setContent(getRemoteView(title,msg))


        val notificationManager = NotificationManagerCompat.from(applicationContext)
        notificationManager.notify(Random.nextInt(), builder.build())

        //for the custom sound while receiving high priority msg
        notifChannelCreation()
    }

    private fun removeBrokenChannel() {
        NotificationManagerCompat.from(applicationContext)
            .deleteNotificationChannel(BROKEN_CHANNEL_ID)
    }


    companion object {
        const val BROKEN_CHANNEL_ID: String = "general_channel"
        const val CHANNEL_ID: String = "general_channel_new"
    }
}