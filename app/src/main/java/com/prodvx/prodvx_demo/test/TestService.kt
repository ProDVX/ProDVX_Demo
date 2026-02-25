package com.prodvx.prodvx_demo.test

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.prodvx.prodvx_demo.R

class TestService : Service() {
	
	override fun onBind(intent: Intent?): IBinder? {
		return null
	}
	
	override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
		when (intent?.action) {
			Actions.START.toString() -> start()
			Actions.STOP.toString() -> stopSelf()
		}
		
		return super.onStartCommand(intent, flags, startId)
	}
	private fun start() {
		val notification = NotificationCompat.Builder(this, "test_channel")
			.setContentTitle("Test Service")
			.setContentText("Test Service is running")
			.setSmallIcon(R.drawable.ic_launcher_foreground)
			.setOngoing(true)
			.build()
		
		startForeground(1, notification)
	}
	
	enum class Actions {
		START, STOP
	}
}