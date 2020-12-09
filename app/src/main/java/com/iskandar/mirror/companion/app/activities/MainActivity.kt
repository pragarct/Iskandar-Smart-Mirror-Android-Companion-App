package com.iskandar.mirror.companion.app.activities

import android.os.Bundle
import android.view.View
import com.iskandar.mirror.companion.app.R
import com.iskandar.mirror.companion.app.classes.BaseActivity
import com.iskandar.mirror.companion.app.classes.makeClearableEditText
import com.iskandar.mirror.companion.app.classes.onRightDrawableClicked
import com.iskandar.mirror.companion.library.FactorialCalculator
import com.iskandar.mirror.companion.library.android.NotificationUtil
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    private val notificationUtil: NotificationUtil by lazy { NotificationUtil(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button_compute.setOnClickListener {
            val input = edit_text_factorial.text.toString().toInt()
            val result = FactorialCalculator.computeFactorial(input).toString()

            text_result.text = result
            text_result.visibility = View.VISIBLE

            notificationUtil.showNotification(
                context = this,
                title = getString(R.string.notification_title),
                message = result
            )
        }

        // Add clear buttons to EditTexts, and drawableEnd in xml allow a clear button to be made
        addRightCancelDrawable(edit_text_factorial)
        edit_text_factorial.onRightDrawableClicked { it.text.clear() }
        edit_text_factorial.makeClearableEditText(null, null)
    }
}
