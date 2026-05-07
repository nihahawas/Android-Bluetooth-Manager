package com.example.bluetooth2

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private var bAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private lateinit var statusTv: TextView
    private lateinit var devicesListView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Initialize UI Elements
        statusTv = findViewById(R.id.statusTextView)
        devicesListView = findViewById(R.id.devicesListView)
        val btnOn: Button = findViewById(R.id.btnOn)
        val btnOff: Button = findViewById(R.id.btnOff)
        val btnDiscoverable: Button = findViewById(R.id.btnDiscoverable)
        val btnPaired: Button = findViewById(R.id.btnPairedDevices)

        // 1. Check if Bluetooth is supported
        if (bAdapter == null) {
            statusTv.text = "Bluetooth is not supported on this device"
        } else {
            statusTv.text = "Bluetooth is available"
        }

        // 2. Turn On Bluetooth
        btnOn.setOnClickListener {
            if (bAdapter?.isEnabled == false) {
                val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                // Check permissions for Android 12+
                if (checkBTPermissions()) {
                    startActivityForResult(intent, 0)
                    Toast.makeText(this, "Turning On...", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Bluetooth is already on", Toast.LENGTH_SHORT).show()
            }
        }

        // 3. Turn Off Bluetooth
        btnOff.setOnClickListener {
            if (checkBTPermissions()) {
                bAdapter?.disable()
                statusTv.text = "Bluetooth Status: Off"
                Toast.makeText(this, "Bluetooth Turned Off", Toast.LENGTH_SHORT).show()
            }
        }

        // 4. Get Visible (Discoverable)
        btnDiscoverable.setOnClickListener {
            if (checkBTPermissions()) {
                val intent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
                intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
                startActivity(intent)
            }
        }

        // 5. List Paired Devices
        btnPaired.setOnClickListener {
            if (checkBTPermissions()) {
                val pairedDevices: Set<BluetoothDevice>? = bAdapter?.bondedDevices
                val list = ArrayList<String>()

                if (pairedDevices != null && pairedDevices.isNotEmpty()) {
                    for (device in pairedDevices) {
                        list.add("${device.name}\n${device.address}")
                    }
                    val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list)
                    devicesListView.adapter = adapter
                } else {
                    Toast.makeText(this, "No paired devices found", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Helper function to handle Android 12+ permissions
    private fun checkBTPermissions(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN), 1)
                return false
            }
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                statusTv.text = "Bluetooth Status: On"
            } else {
                statusTv.text = "Bluetooth Status: User Denied"
            }
        }
    }
}