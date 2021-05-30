package com.example.travelapp

import android.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat


class BroReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("wejszlo","onReceive")
        val notificationId = 69
        val notificationManager =
            context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "geo"
        val name: CharSequence = "Geofencing channel"
        val desc = "Used for geofencing"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val mChannel = NotificationChannel(channelId, name, importance).apply {
            description = desc
            enableLights(true)
            lightColor = Color.GREEN
            enableVibration(true)
            vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            setShowBadge(true)
        }
        notificationManager.createNotificationChannel(mChannel)
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(context, channelId)
//            .setSmallIcon(R.drawable.)
            .setContentTitle("Flashback")
            .setContentText("Remember that?")

        val resultIntent = Intent(context, MainActivity::class.java)
        val stackBuilder: TaskStackBuilder = TaskStackBuilder.create(context)
        stackBuilder.addParentStack(MainActivity::class.java)
        stackBuilder.addNextIntent(resultIntent)
        val resultPendingIntent: PendingIntent =
            stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        builder.setContentIntent(resultPendingIntent)
        notificationManager.notify(notificationId, builder.build())
    }
}