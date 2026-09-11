package com.example.core.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.AuraApplication
import com.example.MainActivity
import com.example.R
import java.util.Locale

class TimerForegroundService : Service() {

    companion object {
        const val CHANNEL_ID = "aura_focus_timer_channel"
        const val NOTIFICATION_ID = 1001

        const val ACTION_START = "com.example.action.START"
        const val ACTION_UPDATE = "com.example.action.UPDATE"
        const val ACTION_PAUSE = "com.example.action.PAUSE"
        const val ACTION_RESUME = "com.example.action.RESUME"
        const val ACTION_SKIP = "com.example.action.SKIP"
        const val ACTION_STOP = "com.example.action.STOP"

        const val EXTRA_REMAINING_SECONDS = "extra_remaining_seconds"
        const val EXTRA_TASK_TITLE = "extra_task_title"
        const val EXTRA_IS_RUNNING = "extra_is_running"

        fun startOrUpdate(
            context: Context,
            remainingSeconds: Int,
            taskTitle: String,
            isRunning: Boolean
        ) {
            val intent = Intent(context, TimerForegroundService::class.java).apply {
                action = ACTION_UPDATE
                putExtra(EXTRA_REMAINING_SECONDS, remainingSeconds)
                putExtra(EXTRA_TASK_TITLE, taskTitle)
                putExtra(EXTRA_IS_RUNNING, isRunning)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            val intent = Intent(context, TimerForegroundService::class.java).apply {
                action = ACTION_STOP
            }
            context.startService(intent)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action

        when (action) {
            ACTION_PAUSE -> {
                val app = application as? AuraApplication
                app?.container?.timerEngine?.pause()
            }
            ACTION_RESUME -> {
                val app = application as? AuraApplication
                app?.container?.timerEngine?.resume()
            }
            ACTION_SKIP -> {
                val app = application as? AuraApplication
                app?.container?.timerEngine?.skip()
            }
            ACTION_STOP -> {
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
                return START_NOT_STICKY
            }
            ACTION_UPDATE, ACTION_START, null -> {
                val remainingSeconds = intent?.getIntExtra(EXTRA_REMAINING_SECONDS, 25 * 60) ?: (25 * 60)
                val taskTitle = intent?.getStringExtra(EXTRA_TASK_TITLE) ?: "Focus Session"
                val isRunning = intent?.getBooleanExtra(EXTRA_IS_RUNNING, false) ?: false

                if (!isRunning && remainingSeconds <= 0) {
                    stopForeground(STOP_FOREGROUND_REMOVE)
                    stopSelf()
                    return START_NOT_STICKY
                }

                val notification = buildNotification(remainingSeconds, taskTitle, isRunning)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    startForeground(
                        NOTIFICATION_ID,
                        notification,
                        ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
                    )
                } else {
                    startForeground(NOTIFICATION_ID, notification)
                }
            }
        }

        return START_STICKY
    }

    private fun buildNotification(remainingSeconds: Int, taskTitle: String, isRunning: Boolean): Notification {
        val minutes = remainingSeconds / 60
        val seconds = remainingSeconds % 60
        val timeFormatted = String.format(Locale.US, "%02d:%02d", minutes, seconds)

        val openAppIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val openAppPendingIntent = PendingIntent.getActivity(
            this,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val toggleActionIntent = Intent(this, TimerForegroundService::class.java).apply {
            action = if (isRunning) ACTION_PAUSE else ACTION_RESUME
        }
        val togglePendingIntent = PendingIntent.getService(
            this,
            1,
            toggleActionIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val skipActionIntent = Intent(this, TimerForegroundService::class.java).apply {
            action = ACTION_SKIP
        }
        val skipPendingIntent = PendingIntent.getService(
            this,
            2,
            skipActionIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val toggleLabel = if (isRunning) "PAUSE" else "RESUME"

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("POMODIUS [$timeFormatted]")
            .setContentText(taskTitle)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(openAppPendingIntent)
            .setOngoing(isRunning)
            .setOnlyAlertOnce(true)
            .addAction(0, toggleLabel, togglePendingIntent)
            .addAction(0, "SKIP", skipPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Pomodoro Focus Timer",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows remaining Pomodoro time and session controls"
                setShowBadge(false)
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}
