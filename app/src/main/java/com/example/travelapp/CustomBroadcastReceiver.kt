package com.example.travelapp


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
import com.google.android.gms.location.GeofencingEvent
import kotlin.concurrent.thread


@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class CustomBroadcastReceiver : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context?, intent: Intent?) {
        val notificationId = 1
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
            .setSmallIcon(R.drawable.photo_icon)
            .setContentTitle("You have been here before!")
            .setContentText("Welcome back ;)")

        val geoEvent = GeofencingEvent.fromIntent(intent)
        if (geoEvent.hasError()) {
            return
        }

        val trigger = geoEvent.triggeringGeofences[0].requestId
        thread {
            Shared.database?.photoDao?.selectByUri(trigger)?.let {
                val resultIntent = Intent(context, PhotoDetailActivity::class.java)
                resultIntent.putExtra("photoUri", trigger)
                resultIntent.putExtra("comment",it.comment)
                val stackBuilder: TaskStackBuilder = TaskStackBuilder.create(context)
                stackBuilder.addParentStack(AddCommentActivity::class.java)
                stackBuilder.addNextIntent(resultIntent)
                val resultPendingIntent: PendingIntent =
                    stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
                builder.setContentIntent(resultPendingIntent)
                notificationManager.notify(notificationId, builder.build())
            }
        }
    }
}