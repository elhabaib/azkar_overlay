package com.azkar.overlay

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.media.MediaPlayer
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageView
import androidx.core.app.NotificationCompat

class OverlayService : Service() {

    private lateinit var windowManager: WindowManager
    private var overlayView: View? = null
    private val handler = Handler(Looper.getMainLooper())

    // ===== غيّر الرقم ده لو عايز فترة مختلفة (بالميلي ثانية) =====
    private val intervalMillis = 10 * 60 * 1000L // 10 دقايق

    private var mediaPlayer: MediaPlayer? = null

    private val reminderRunnable = object : Runnable {
        override fun run() {
            showReminder()
            handler.postDelayed(this, intervalMillis)
        }
    }

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        startForeground(1, buildNotification())
        // أول ظهور بعد 10 دقايق من بدء التشغيل
        handler.postDelayed(reminderRunnable, intervalMillis)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    private fun buildNotification(): Notification {
        val channelId = "azkar_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "تذكير الصلاة على النبي",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("تذكير الصلاة على النبي ﷺ")
            .setContentText("الخدمة شغالة في الخلفية")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setOngoing(true)
            .build()
    }

    private fun showReminder() {
        if (overlayView != null) return // لسه ظاهر من قبل، متعملش نسخة تانية

        val inflater = LayoutInflater.from(this)
        overlayView = inflater.inflate(R.layout.overlay_layout, null)

        val overlayType =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                @Suppress("DEPRECATION")
                WindowManager.LayoutParams.TYPE_PHONE

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            overlayType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        )
        // مكان ظهور الدايرة على الشاشة - تقدر تغيّرها
        params.gravity = Gravity.BOTTOM or Gravity.START
        params.x = 40
        params.y = 140

        windowManager.addView(overlayView, params)

        // تدوير الصورة باستمرار
        val image = overlayView!!.findViewById<ImageView>(R.id.overlayImage)
        val rotate = RotateAnimation(
            0f, 360f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )
        rotate.duration = 6000
        rotate.repeatCount = Animation.INFINITE
        rotate.interpolator = LinearInterpolator()
        image.startAnimation(rotate)

        // زرار الإغلاق (علامة ✕)
        overlayView!!.findViewById<View>(R.id.overlayClose).setOnClickListener {
            removeOverlay()
        }

        playSound()
    }

    private fun playSound() {
        try {
            // بيدور على ملف اسمه azkar_sound في مجلد res/raw
            val resId = resources.getIdentifier("azkar_sound", "raw", packageName)
            if (resId != 0) {
                mediaPlayer?.release()
                mediaPlayer = MediaPlayer.create(this, resId)
                mediaPlayer?.setOnCompletionListener { it.release() }
                mediaPlayer?.start()
            }
            // لو مفيش ملف صوت مضاف، مفيش صوت هيتشغل - راجع ملف README
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun removeOverlay() {
        overlayView?.let {
            try {
                windowManager.removeView(it)
            } catch (e: Exception) {
                // العنصر ممكن يكون اتشال بالفعل
            }
        }
        overlayView = null

        mediaPlayer?.let {
            if (it.isPlaying) it.stop()
            it.release()
        }
        mediaPlayer = null
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(reminderRunnable)
        removeOverlay()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
