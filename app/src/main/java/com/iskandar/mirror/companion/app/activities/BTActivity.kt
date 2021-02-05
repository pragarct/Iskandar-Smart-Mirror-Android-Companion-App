package com.iskandar.mirror.companion.app.activities

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import com.iskandar.mirror.companion.app.R
import com.iskandar.mirror.companion.app.classes.BaseActivity

const val REQUEST_ENABLE_BT = 1

class BTActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bt)

        val names = mutableListOf<String>()
        val addresses = mutableListOf<String>()

        val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
        val listView = findViewById<ListView>(R.id.lv_visible)
        val btnVisible = findViewById<Button>(R.id.btn_visible)
        btnVisible.setOnClickListener() {
            // find devices that have already been bonded to
            val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
            pairedDevices?.forEach { device ->
                names.add(device.name)
                addresses.add(device.address)
            }

            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, names)
            listView.adapter = adapter
        }

        if (bluetoothAdapter?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }
    }
}
