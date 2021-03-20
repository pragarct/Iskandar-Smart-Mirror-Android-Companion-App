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
import com.iskandar.mirror.companion.app.classes.makeClearableEditText
import com.iskandar.mirror.companion.app.classes.onRightDrawableClicked
import kotlinx.android.synthetic.main.activity_bluetooth.*
import kotlinx.android.synthetic.main.activity_bluetooth.nav_view
import java.io.IOException
import java.io.InputStream
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
    private var mmInStream: InputStream? = null

    private var initialSetup = true

    companion object {
        var new_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        var bluetoothSocket: BluetoothSocket? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bluetooth)

        val initialSetupMaybeNull: Boolean? = intent.extras?.getBoolean("initialSetup")
        if (initialSetupMaybeNull != null) {
            initialSetup = initialSetupMaybeNull
        }

        if (initialSetup) {
            title = getString(R.string.iskandar_initial_setup)
        } else {
            setUpNavigationBar()
            // Highlights the item in the navigation bar
            nav_view.setCheckedItem(R.id.nav_bluetooth)
        }

        // Setup EditTexts
        addRightCancelDrawable(editText_SSID)
        editText_SSID.onRightDrawableClicked { it.text.clear() }
        editText_SSID.makeClearableEditText(null, null)
        addRightCancelDrawable(editText_Password)
        editText_Password.onRightDrawableClicked { it.text.clear() }
        editText_Password.makeClearableEditText(null, null)

        setUpNavigationBar()
        // Highlights the item in the navigation bar
        nav_view.setCheckedItem(R.id.nav_bluetooth)

        val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

        listView = findViewById(R.id.lv_visible)
        val discDev = findViewById<TextView>(R.id.discDev)
        val btnVisible = findViewById<Button>(R.id.btn_visible)
        val btnEnterWifi = findViewById<TextView>(R.id.btn_enter_wifi)
        val etSSID = findViewById<TextView>(R.id.editText_SSID)
        val etPassword = findViewById<TextView>(R.id.editText_Password)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, names)

        btn_enter_wifi.setOnClickListener {
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
                            Toast.makeText(this, getString(R.string.bt_connection_success), Toast.LENGTH_SHORT).show()

                            var tmpOut: OutputStream? = null

                            // Get the output stream, using temp objects because
                            // member streams are final
                            try {
                                tmpOut = socket.outputStream
                            } catch (e: IOException) {
                                Toast.makeText(
                                    this,
                                    getString(R.string.bt_output_stream_failure),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            mmOutStream = tmpOut

                            val ssidString = "SSID:" + etSSID.text.toString() // Add SSID:

                            val passString = "PASS:" + etPassword.text.toString() // Add PASS:

                            val ssidByteArray = ssidString.toByteArray()

                            val passByteArray = passString.toByteArray()

                            try {
                                mmOutStream?.write(ssidByteArray)
                            } catch (e: IOException) {
                                Toast.makeText(this, getString(R.string.bt_ssid_failure), Toast.LENGTH_SHORT).show()
                            }

                            try {
                                mmOutStream?.write(passByteArray)
                            } catch (e: IOException) {
                                Toast.makeText(this, getString(R.string.bt_password_failure), Toast.LENGTH_SHORT).show()
                            }

                            var tmpIn: InputStream? = null

                            // Get the input stream, using temp objects because
                            // member streams are final
                            try {
                                tmpIn = socket.inputStream
                            } catch (e: IOException) {
                                Toast.makeText(
                                    this,
                                    getString(R.string.bt_input_stream_failure),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            mmInStream = tmpIn

                            var ipAddress: ByteArray? = null

                            try {
                                ipAddress = mmInStream?.readBytes()
                            } catch (e: IOException) {
                                Toast.makeText(
                                    this,
                                    getString(R.string.bt_ip_address_failure),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            var ipString = ipAddress.toString()

                            ipString = "http://$ipString:5000/"

                            setIPAddress(ipString)

                            Toast.makeText(this, getString(R.string.bt_sent_info_success), Toast.LENGTH_SHORT).show()

                            val intent: Intent = if (initialSetup) {
                                Intent(this, LocationActivity::class.java)
                            } else {
                                Intent(this, HomeActivity::class.java)
                            }

                            startActivity(intent)
                        }
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this, getString(R.string.bt_connection_failure), Toast.LENGTH_SHORT).show()
                }
            }
        }

        if (bluetoothAdapter?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }
    }
}
