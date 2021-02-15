package com.iskandar.mirror.companion.app.activities.ui

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.os.Bundle
import android.widget.*
import com.iskandar.mirror.companion.app.R
import com.iskandar.mirror.companion.app.classes.BaseActivity
import kotlinx.android.synthetic.main.activity_change_location.*
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

const val REQUEST_ENABLE_BT = 1

val names = mutableListOf<String>()
val addresses = mutableListOf<String>()
val list : ArrayList<BluetoothDevice> = ArrayList()
lateinit var adapter: ArrayAdapter<BluetoothDevice>
lateinit var listView: ListView

class BluetoothActivity : BaseActivity() {

    companion object{
        var new_UUID:UUID = UUID.fromString("89c76f22-bd18-498b-a29a-ed05eca10a1c")
        var bluetoothSocket:BluetoothSocket? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bluetooth)
        setUpNavigationBar()
        // Highlights the item in the navigation bar
        nav_view.setCheckedItem(R.id.nav_bluetooth)

        val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

        listView = findViewById<ListView>(R.id.lv_visible)
        val discDev = findViewById<TextView>(R.id.discDev)
        val btnVisible = findViewById<Button>(R.id.btn_visible)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list)

        btnVisible.setOnClickListener() {
            // find devices that have already been bonded to
            val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
            pairedDevices?.forEach { device ->
                names.add(device.name)
                addresses.add(device.address)
                list.add(device)
            }

            discDev.visibility = TextView.VISIBLE
            listView.adapter = adapter

            listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                val device:BluetoothDevice = list[position]

                val mmSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
                    device.createRfcommSocketToServiceRecord(new_UUID)
                }

                try {
                    if (bluetoothSocket == null) {
                        bluetoothAdapter?.cancelDiscovery()
                        mmSocket?.use { socket ->
                            // Connect to the remote device through the socket. This call blocks
                            // until it succeeds or throws an exception.
                            socket.connect()
                            Toast.makeText(applicationContext, "Connected", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: IOException){
                    e.printStackTrace()
                    Toast.makeText(applicationContext, "Not Connected", Toast.LENGTH_SHORT).show()
                }
            }


        }

        if (bluetoothAdapter?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }
    }
}
