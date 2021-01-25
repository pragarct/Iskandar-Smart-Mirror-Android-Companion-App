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

        button_compute.setOnClickListener {
            val executor = Executors.newSingleThreadExecutor()
            val handler = Handler(Looper.getMainLooper())

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
}
