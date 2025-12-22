package com.prodvx.prodvx_demo

import android.app.admin.DeviceAdminReceiver
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent


class MyDeviceAdminReceiver : DeviceAdminReceiver() {
	override fun onEnabled(context: Context, intent: Intent) {
		super.onEnabled(context, intent)
		// App is now a Device Admin
	}
	
	override fun onProfileProvisioningComplete(context: Context, intent: Intent) {
		super.onProfileProvisioningComplete(context, intent)
		// This is called when the app becomes a Device Owner
		val manager = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
		val componentName = ComponentName(context, MyDeviceAdminReceiver::class.java)
		manager.setProfileName(componentName, "My MDM Profile")
	}
	
}