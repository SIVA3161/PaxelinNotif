package com.paxel.paxelinnotif

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * Created by Siva G Gurusamy on 04,Feb,2022
 * email : siva@paxel.co
 */

const val channelId = "notif_channel"
const val channelName = "com.paxel.paxelinnotif"
@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class MyFirebaseMessagingService : FirebaseMessagingService() {
    // generate notification
    // mount the created notif with custom layout
    // show notif

    override fun onMessageReceived(remoteMsg: RemoteMessage) {
        if(remoteMsg.notification != null) {
            generateNotif(remoteMsg.notification!!.title!!,remoteMsg.notification!!.body!!)
        }
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
            .setContentIntent(pendingIntent)
        builder = builder.setContent(getRemoteView(title,msg))

        val notifMngr = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notifChannel = NotificationChannel(channelId, channelName,NotificationManager.IMPORTANCE_HIGH)
            notifMngr.createNotificationChannel(notifChannel)
        }
        notifMngr.notify(0,builder.build())
    }
}