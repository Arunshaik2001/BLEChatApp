package com.example.learningble

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.learningble.bluetooth.ChatServer
import com.example.learningble.presentation.ChatCompose
import com.example.learningble.presentation.DeviceScanCompose
import com.example.learningble.states.DeviceConnectionState
import com.example.learningble.ui.theme.LearningBLETheme
import com.example.learningble.viewmodels.DeviceScanViewModel
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

private const val TAG = "MainActivityTAG"

class MainActivity : ComponentActivity() {

    private val viewModel: DeviceScanViewModel by viewModels()

    override fun onStop() {
        super.onStop()
        ChatServer.stopServer()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            LearningBLETheme {
                val result = remember { mutableStateOf<Int?>(100) }
                val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                    result.value = it.resultCode
                }

                LaunchedEffect(key1 = true){

                    Dexter.withContext(this@MainActivity)
                        .withPermissions(
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.BLUETOOTH_ADVERTISE,
                            Manifest.permission.BLUETOOTH_CONNECT,
                            Manifest.permission.BLUETOOTH_SCAN,
                            Manifest.permission.BLUETOOTH,
                            Manifest.permission.BLUETOOTH_ADMIN,
                        )
                        .withListener(object : MultiplePermissionsListener {
                            override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                                val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                                launcher.launch(intent)
                            }

                            override fun onPermissionRationaleShouldBeShown(
                                permissions: List<PermissionRequest?>?,
                                token: PermissionToken?
                            ) {

                            }
                        })
                        .check()

                }

                LaunchedEffect(key1 = result.value){
                    if(result.value == RESULT_OK){
                        ChatServer.startServer(application)
                        viewModel.startScan()
                    }
                }

                Scaffold(topBar = {
                    TopAppBar(
                        title = {
                            Text(text = "Bluetooth Chat App")
                        },
                        backgroundColor = MaterialTheme.colors.primary,
                        contentColor = Color.White,
                        elevation = 10.dp
                    )
                }) {


                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp),
                        color = MaterialTheme.colors.background
                    ) {
                        val deviceScanningState by viewModel.viewState.observeAsState()

                        val deviceConnectionState by ChatServer.deviceConnection.observeAsState()

                        var isChatOpen by remember {
                            mutableStateOf(false)
                        }

                        Box(
                            contentAlignment = Alignment.TopCenter,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            if (deviceScanningState != null && !isChatOpen || deviceConnectionState == DeviceConnectionState.Disconnected) {
                                Column {
                                    Text(
                                        text = "Choose a device to chat with:",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    DeviceScanCompose.DeviceScan(deviceScanViewState = deviceScanningState!!) {
                                        isChatOpen = true
                                    }
                                }

                            } else if (deviceScanningState != null && deviceConnectionState is DeviceConnectionState.Connected) {
                                ChatCompose.Chats((deviceConnectionState as DeviceConnectionState.Connected).device.name)
                            } else {
                                Text(text = "Nothing")
                            }
                        }
                    }
                }
            }
        }

    }
}
