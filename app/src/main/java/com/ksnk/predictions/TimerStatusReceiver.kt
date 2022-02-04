package com.ksnk.predictions

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.TextView


class TimerStatusReceiver(var textView:TextView) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null && intent.action == CountdownTimerService.TIME_INFO) {
            if (intent.hasExtra("VALUE")) {
                textView.text = intent.getStringExtra("VALUE");
            }
        }
    }
}