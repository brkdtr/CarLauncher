package com.example.carplaylauncher

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log

class BluetoothReceiver : BroadcastReceiver() {

    private val TARGET_BLUETOOTH_NAME = "TOYOTA"

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)

        val deviceName = device?.name ?: ""
        
        Log.d("BluetoothReceiver", "Action: $action, Device: $deviceName")

        if (deviceName.contains(TARGET_BLUETOOTH_NAME, ignoreCase = true)) {
            when (action) {
                BluetoothDevice.ACTION_ACL_CONNECTED -> {
                    if (Settings.canDrawOverlays(context)) {
                        val launchIntent = Intent(context, MainActivity::class.java).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        }
                        context.startActivity(launchIntent)
                    }
                }
                BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                    val closeIntent = Intent("com.example.carplaylauncher.CLOSE_APP")
                    context.sendBroadcast(closeIntent)
                }
            }
        }
    }
}
