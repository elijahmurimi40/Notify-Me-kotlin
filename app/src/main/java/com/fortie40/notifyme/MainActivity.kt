package com.fortie40.notifyme

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        const val PRIMARY_CHANNEL_ID = "primary_notification_channel"
        const val NOTIFICATION_ID = 0
    }

    private lateinit var mNotifyManager: NotificationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        // create notification channel
        createNotificationChannel()

        notify.setOnClickListener {
            sendNotifications()
        }
    }

    private fun createNotificationChannel() {
        mNotifyManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(PRIMARY_CHANNEL_ID,
                getString(R.string.notify_me_notification),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                    enableLights(true)
                    lightColor = Color.RED
                    enableVibration(true)
                    description = "Notification from Notify Me"
                }

            mNotifyManager.createNotificationChannel(channel)
        }
    }

    private fun getNotificationBuilder(): NotificationCompat.Builder {
        return NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(getString(R.string.notification_text))
            .setSmallIcon(R.drawable.ic_notification_icon)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
    }

    private fun sendNotifications() {
        val notifyBuilder = getNotificationBuilder()
        mNotifyManager.notify(NOTIFICATION_ID, notifyBuilder.build())
    }
}
