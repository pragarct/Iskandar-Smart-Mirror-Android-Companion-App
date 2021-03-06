package com.iskandar.mirror.companion.app.activities.ui

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.iskandar.mirror.companion.app.R
import com.iskandar.mirror.companion.app.classes.BaseActivity
import kotlinx.android.synthetic.main.activity_bluetooth.*
import java.io.IOException
import java.io.OutputStream
import java.util.UUID
import kotlin.collections.ArrayList

const val REQUEST_ENABLE_BT = 1

val names = mutableListOf<String>()
val addresses = mutableListOf<String>()
val list: ArrayList<BluetoothDevice> = ArrayList()
lateinit var adapter: ArrayAdapter<String>

class BluetoothActivity : BaseActivity() {

    lateinit var listView: ListView

    private var mmOutStream: OutputStream? = null

    companion object {
        var new_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        var bluetoothSocket: BluetoothSocket? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bluetooth)
        setUpNavigationBar()
        // Highlights the item in the navigation bar
        nav_view.setCheckedItem(R.id.nav_bluetooth)

        val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

        listView = findViewById(R.id.lv_visible)
        val discDev = findViewById<TextView>(R.id.discDev)
        val btnVisible = findViewById<Button>(R.id.btn_visible)
        val wifiHead = findViewById<TextView>(R.id.wifi_header)
        val ssidTxt = findViewById<TextView>(R.id.ssid)
        val passwordTxt = findViewById<TextView>(R.id.password)
        val btnEnterWifi = findViewById<TextView>(R.id.btn_enter_wifi)
        val etSSID = findViewById<TextView>(R.id.editText_SSID)
        val etPassword = findViewById<TextView>(R.id.editText_Password)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, names)

        btn_enter_wifi.setOnClickListener {
            wifiHead.visibility = TextView.INVISIBLE
            ssidTxt.visibility = TextView.INVISIBLE
            passwordTxt.visibility = TextView.INVISIBLE
            btnEnterWifi.visibility = Button.INVISIBLE
            etSSID.visibility = EditText.INVISIBLE
            etPassword.visibility = EditText.INVISIBLE

            btnVisible.visibility = Button.VISIBLE
        }

        btnVisible.setOnClickListener {
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
                val device: BluetoothDevice = list[position]

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

                            var tmpOut: OutputStream? = null

                            // Get the input and output streams, using temp objects because
                            // member streams are final

                            // Get the input and output streams, using temp objects because
                            // member streams are final
                            try {
                                tmpOut = socket.outputStream
                            } catch (e: IOException) {
                                Toast.makeText(applicationContext, "Failed", Toast.LENGTH_SHORT).show()
                            }

                            mmOutStream = tmpOut

                            val testingString = "THIS IS A TEST"

                            val bytes = testingString.toByteArray() // converts entered String into bytes

                            try {
                                mmOutStream!!.write(bytes)
                            } catch (e: IOException) {
                                Toast.makeText(applicationContext, "Failed to send.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } catch (e: IOException) {
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
