package com.iskandar.mirror.companion.app.activities.ui

import android.os.Bundle
import com.iskandar.mirror.companion.app.R
import com.iskandar.mirror.companion.app.classes.BaseActivity
import kotlinx.android.synthetic.main.activity_background_overview.*

class BackgroundOverviewActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_background_overview)
        setUpNavigationBar()
        // Highlights the item in the navigation bar
        nav_view.setCheckedItem(R.id.nav_background)
    }
}
