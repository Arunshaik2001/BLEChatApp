package com.example.learningble.presentation

import android.bluetooth.BluetoothDevice
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.learningble.bluetooth.ChatServer
import com.example.learningble.states.DeviceScanViewState

private const val TAG = "DeviceScanCompose"

object DeviceScanCompose {


    @Composable
    fun ShowDevices(
        scanResults: Map<String, BluetoothDevice>,
        onClick: (BluetoothDevice?) -> Unit
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
        ) {
            itemsIndexed(scanResults.keys.toList()) { _, key ->
                Column {
                    Column(
                        modifier = Modifier.clickable {
                            val device: BluetoothDevice? = scanResults.get(key = key)
                            onClick(device)
                        }
                            .background(Color.LightGray, shape = RoundedCornerShape(10.dp))
                            .fillMaxWidth()
                            .border(1.dp, Color.Black, shape = RoundedCornerShape(10.dp))
                            .padding(5.dp)
                    ) {
                        Text(
                            text = scanResults[key]?.name ?: "Unknown Device",
                            )
                        Spacer(modifier = Modifier.size(10.dp))
                        Text(
                            text = scanResults[key]?.address ?: "",
                            fontWeight = FontWeight.Light
                        )
                    }
                    Spacer(modifier = Modifier.size(10.dp))
                }
            }
        }
    }

    @Composable
    fun DeviceScan(deviceScanViewState: DeviceScanViewState, onDeviceSelected: () -> Unit) {
        when (deviceScanViewState) {
            is DeviceScanViewState.ActiveScan -> {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(15.dp))
                        Text(
                            text = "Scanning for devices",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Light
                        )
                    }
                }

            }
            is DeviceScanViewState.ScanResults -> {
                ShowDevices(scanResults = deviceScanViewState.scanResults, onClick = {
                    Log.i(TAG, "Device Selected ${it!!.name ?: ""}")
                    ChatServer.setCurrentChatConnection(device = it!!)
                    onDeviceSelected()
                })
            }
            is DeviceScanViewState.Error -> {
                Text(text = deviceScanViewState.message)
            }
            else -> {
                Text(text = "Nothing")
            }
        }
    }
}