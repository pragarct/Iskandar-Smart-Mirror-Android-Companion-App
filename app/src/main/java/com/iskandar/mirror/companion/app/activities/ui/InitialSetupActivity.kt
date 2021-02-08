package com.iskandar.mirror.companion.app.activities.ui

import android.os.Bundle
import com.iskandar.mirror.companion.app.R
import com.iskandar.mirror.companion.app.classes.InformationBaseActivity

class InitialSetupActivity : InformationBaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_initial_setup)
        // This is a method in InformationBaseActivity used also in ChangeInformationActivity
        setupListenersAndEditTexts()
    }
}
