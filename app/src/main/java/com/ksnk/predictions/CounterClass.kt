package com.ksnk.predictions

import android.content.Context
import android.content.Intent
import android.os.CountDownTimer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.ksnk.predictions.CountdownTimerService.Companion.TIME_INFO
import java.lang.String
import java.util.concurrent.TimeUnit


class CounterClass(millisInFuture: Long, countDownInterval: Long, context: Context) :
    CountDownTimer(millisInFuture, countDownInterval) {
    var context = context
    override fun onTick(millisUntilFinished: Long) {
        val hms = String.format(
            "%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(
                millisUntilFinished
            ),
            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(
                TimeUnit.MILLISECONDS.toHours(
                    millisUntilFinished
                )
            ),
            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                TimeUnit.MILLISECONDS.toMinutes(
                    millisUntilFinished
                )
            )
        )
        println(hms)
        val timerInfoIntent = Intent(TIME_INFO)
        timerInfoIntent.putExtra("VALUE", hms)
        LocalBroadcastManager.getInstance(context).sendBroadcast(timerInfoIntent)
    }

    override fun onFinish() {
        val timerInfoIntent = Intent(TIME_INFO)
        timerInfoIntent.putExtra("VALUE", "Completed")
        LocalBroadcastManager.getInstance(context).sendBroadcast(timerInfoIntent)
    }
}