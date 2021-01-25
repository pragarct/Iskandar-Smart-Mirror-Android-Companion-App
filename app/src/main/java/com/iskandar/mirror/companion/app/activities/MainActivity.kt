package com.iskandar.mirror.companion.app.activities

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.iskandar.mirror.companion.app.R
import com.iskandar.mirror.companion.app.classes.BaseActivity
import com.iskandar.mirror.companion.app.classes.makeClearableEditText
import com.iskandar.mirror.companion.app.classes.onRightDrawableClicked
import com.iskandar.mirror.companion.library.android.NotificationUtil
import kotlinx.android.synthetic.main.activity_main.*
import java.net.URL
import java.util.concurrent.Executors

class MainActivity : BaseActivity() {

    private val notificationUtil: NotificationUtil by lazy { NotificationUtil(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*button_compute.setOnClickListener {
            val input = edit_text_factorial.text.toString().toInt()
            val result = FactorialCalculator.computeFactorial(input).toString()

            text_result.text = result
            text_result.visibility = View.VISIBLE

            notificationUtil.showNotification(
                context = this,
                title = getString(R.string.notification_title),
                message = result
            )
        }*/

        button_compute.setOnClickListener {
            // TODO: Supposedly executors and handlers aren't a great way to handle this, change eventually
            val executor = Executors.newSingleThreadExecutor()
            val handler = Handler(Looper.getMainLooper())
            // TODO: Need to user 10.0.2.2 for the emulator. I have no idea why.

            // TODO: We need to test on an actual device. I have a feeling 10.0.2.2 won't work

            executor.execute {
                val result = URL("http://10.0.2.2:5000/weather").readText()

                handler.post {
                    Log.d(javaClass.simpleName, result)
                }
            }
        }

        // Add clear buttons to EditTexts, and drawableEnd in xml allow a clear button to be made
        addRightCancelDrawable(edit_text_factorial)
        edit_text_factorial.onRightDrawableClicked { it.text.clear() }
        edit_text_factorial.makeClearableEditText(null, null)
    }

    /*fun sendMessage(view: View) {
        // TODO: Supposedly executors and handlers aren't a great way to handle this, change eventually
        val executor = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())
        // TODO: Need to user 10.0.2.2 for the emulator. I have no idea why.

        // TODO: We need to test on an actual device. I have a feeling 10.0.2.2 won't work

        executor.execute {
            val result = URL("http://10.0.2.2:5000/weather").readText()

            handler.post {
                Log.d(javaClass.simpleName, result)
            }
        }
    }*/
}
