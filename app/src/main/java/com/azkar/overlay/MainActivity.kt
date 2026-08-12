package com.azkar.overlay

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val statusText = findViewById<TextView>(R.id.statusText)
        val permissionBtn = findViewById<Button>(R.id.permissionBtn)
        val startBtn = findViewById<Button>(R.id.startBtn)
        val stopBtn = findViewById<Button>(R.id.stopBtn)

        permissionBtn.setOnClickListener {
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                startActivity(intent)
            } else {
                Toast.makeText(this, "الصلاحية متاحة بالفعل ✅", Toast.LENGTH_SHORT).show()
            }
        }

        startBtn.setOnClickListener {
            if (Settings.canDrawOverlays(this)) {
                val serviceIntent = Intent(this, OverlayService::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(serviceIntent)
                } else {
                    startService(serviceIntent)
                }
                statusText.text = "التذكير شغّال الآن ✅ (هيظهر كل ١٠ دقايق)"
            } else {
                Toast.makeText(
                    this,
                    "لازم توافق على صلاحية الظهور فوق التطبيقات الأول",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        stopBtn.setOnClickListener {
            stopService(Intent(this, OverlayService::class.java))
            statusText.text = "التذكير متوقف"
        }
    }
}
