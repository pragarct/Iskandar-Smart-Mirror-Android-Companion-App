package com.iskandar.mirror.companion.app.classes

import android.app.Activity
import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.iskandar.mirror.companion.app.R

open class BaseActivity : AppCompatActivity() {

    /*override fun onCreate(savedInstanceState: Bundle?) {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
        setSupportActionBar(findViewById(R.id.toolbar))

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }*/

    // Close the keyboard on button/screen press and clear focus
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val view = currentFocus
        if (view != null && (ev.action == MotionEvent.ACTION_UP || ev.action == MotionEvent.ACTION_MOVE)) {
            if (view is EditText && !view.javaClass.name.startsWith("android.webkit.")) {
                val scrcoords = IntArray(2)
                view.getLocationOnScreen(scrcoords)
                val x = ev.rawX + view.getLeft() - scrcoords[0]
                val y = ev.rawY + view.getTop() - scrcoords[1]

                if (x < view.getLeft() || x > view.getRight()) {
                    (this.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                        this.window.decorView.applicationWindowToken,
                        0
                    )
                    view.clearFocus()
                }
                if (y < view.getTop() || y > view.getBottom()) {
                    (this.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                        this.window.decorView.applicationWindowToken,
                        0
                    )
                    view.clearFocus()
                }
            }
        }

        return super.dispatchTouchEvent(ev)
    }

    // Close the keyboard when screen is touched (NEED TO LOSE FOCUS TOO)
    /*override fun onTouchEvent(event: MotionEvent): Boolean {
        // Close the keyboard on screen press
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        return true
    }*/

    fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    // Used to close the keyboard - hideKeyboard()
    fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    // Used to add button for clearing EditText
    fun addRightCancelDrawable(editText: EditText) {
        val cancel = ContextCompat.getDrawable(this, R.drawable.cancel)
        cancel?.setBounds(0, 0, cancel.intrinsicWidth, cancel.intrinsicHeight)
        editText.setCompoundDrawables(null, null, cancel, null)
    }
}
