package com.example.lanedodge1.utilities

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.widget.Toast
import java.lang.ref.WeakReference

class SignalManager private constructor(context: Context) {

    private val contextRef = WeakReference(context)

    companion object {
        @Volatile
        private var instance: SignalManager? = null

        fun init(context: Context): SignalManager {
            return instance ?: synchronized(this) {
                instance ?: SignalManager(context).also { instance = it }
            }
        }

        fun getInstance(): SignalManager {
            return instance ?: throw IllegalStateException(
                "SignalManager must be initialized by calling init(context) before use."
            )
        }
    }

    fun toast(text: String) {
        contextRef.get()?.let { context ->
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
        }
    }

    fun vibrate(duration: Long = 300) {
        contextRef.get()?.let { context ->

            val vibrator: Vibrator =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val vibratorManager =
                        context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                    vibratorManager.defaultVibrator
                } else {
                    context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val effect = VibrationEffect.createOneShot(
                    duration,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
                vibrator.vibrate(effect)
            } else {
                vibrator.vibrate(duration)
            }
        }
    }
}
