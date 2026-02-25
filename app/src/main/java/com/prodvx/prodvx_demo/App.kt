package com.prodvx.prodvx_demo

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context

class App: Application() {
	
	override fun onCreate() {
		super.onCreate()
		
		val channel = NotificationChannel(
			"test_channel",
			"Test Channel",
			NotificationManager.IMPORTANCE_HIGH
		)
		val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
		notificationManager.createNotificationChannel(channel)
	}
}