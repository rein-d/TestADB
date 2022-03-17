package com.rein.android.ReynTestApp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

private const val TAG = "AutoStart"
class AutoStart : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Toast.makeText(context, "Service is started!!!!", Toast.LENGTH_SHORT)
        Log.d(TAG,"Service is started!!!!")
    }
}