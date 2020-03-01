package com.fortie40.notifyme

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
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
        const val ACTION_UPDATE_NOTIFICATION = "com.fortie40.notifyme.ACTION_UPDATE_NOTIFICATION"
    }

    private lateinit var mNotifyManager: NotificationManager
    private val mReciver = NotificationReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        // create notification channel
        createNotificationChannel()

        setNotificationButtonStyle(
            isNotifyEnabled = true,
            isUpdateEnabled = false,
            isCancelEnabled = false
        )

        notify.setOnClickListener {
            sendNotifications()
        }

        update.setOnClickListener {
            updateNotifications()
        }

        cancel.setOnClickListener {
            cancelNotifications()
        }

        registerReceiver(mReciver, IntentFilter(ACTION_UPDATE_NOTIFICATION))
    }

    override fun onDestroy() {
        unregisterReceiver(mReciver)
        super.onDestroy()
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
                    description = getString(R.string.notification_from_notify_me)
                }

            mNotifyManager.createNotificationChannel(channel)
        }
    }

    private fun getNotificationBuilder(): NotificationCompat.Builder {
        val notificationIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val notificationPendingIntent = PendingIntent.getActivity(this, NOTIFICATION_ID,
            notificationIntent, 0)
        return NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(getString(R.string.notification_text))
            .setSmallIcon(R.drawable.ic_notification_icon)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentIntent(notificationPendingIntent)
            .setAutoCancel(true)
    }

    private fun sendNotifications() {
        val updateIntent = Intent(ACTION_UPDATE_NOTIFICATION)
        val updatePendingIntent = PendingIntent.getBroadcast(this,
            NOTIFICATION_ID, updateIntent, PendingIntent.FLAG_ONE_SHOT)
        val notifyBuilder = getNotificationBuilder()
        notifyBuilder.addAction(R.drawable.ic_action_name, getString(R.string.update_notification),
            updatePendingIntent)
        mNotifyManager.notify(NOTIFICATION_ID, notifyBuilder.build())
        setNotificationButtonStyle(
            isNotifyEnabled = false,
            isUpdateEnabled = true,
            isCancelEnabled = true
        )
    }

    fun updateNotifications() {
        val image = BitmapFactory.decodeResource(resources, R.drawable.mascot_1)
        val notifyBuilder = getNotificationBuilder()
        notifyBuilder.setStyle(NotificationCompat.BigPictureStyle()
            .bigPicture(image)
            .setBigContentTitle(getString(R.string.notification_updated)))
        mNotifyManager.notify(NOTIFICATION_ID, notifyBuilder.build())
        setNotificationButtonStyle(
            isNotifyEnabled = false,
            isUpdateEnabled = false,
            isCancelEnabled = true
        )

    }

    private fun cancelNotifications() {
        mNotifyManager.cancel(NOTIFICATION_ID)
        setNotificationButtonStyle(
            isNotifyEnabled = true,
            isUpdateEnabled = false,
            isCancelEnabled = false
        )
    }

    private fun setNotificationButtonStyle
                (isNotifyEnabled: Boolean, isUpdateEnabled: Boolean, isCancelEnabled: Boolean) {

        notify.isEnabled = isNotifyEnabled
        update.isEnabled = isUpdateEnabled
        cancel.isEnabled = isCancelEnabled
    }

    inner class NotificationReceiver : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            updateNotifications()
        }
    }
}
