package com.example.learningble.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.learningble.bluetooth.ChatServer
import com.example.learningble.models.Message

private const val TAG = "ChatCompose"

object ChatCompose {


    @Composable
    fun ShowChat(message: Message) {
        Row(
            modifier = Modifier
                .padding(5.dp)
                .fillMaxWidth(),
            horizontalArrangement = if (message is Message.RemoteMessage) Arrangement.Start else Arrangement.End
        ) {
            Box(
                modifier = Modifier
                    .width(150.dp)
                    .padding(5.dp)
                    .border(1.dp, Color.Black, shape = RoundedCornerShape(10.dp))
                    .background(
                        if (message is Message.RemoteMessage) Color(0xFFD3D3D3) else Color(
                            0xFF90EE90
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )
            ) {
                Text(text = message.text, color = Color.Black, modifier = Modifier.padding(10.dp))
            }
        }
    }

    @Composable
    fun Chats(deviceName: String?) {
        val message by ChatServer.messages.observeAsState()

        val inputvalue = remember { mutableStateOf(TextFieldValue()) }

        val messageList = remember {
            mutableStateListOf<Message>()
        }

        if (message != null && !messageList.contains(message)) {
            messageList.add(message!!)
        }



        if (messageList.isNotEmpty()) {
            Column(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "Chat Now with ${deviceName ?: "Unknown"}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))

                Surface(modifier = Modifier
                    .padding(all = Dp(5f))
                    .fillMaxHeight(fraction = 0.85f)) {
                    ChatsList(messageList)
                }


                InputField(inputvalue)
            }
        } else {
            Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize(fraction = 0.85f)
                ) {
                    Text(text = "No Chat History")
                }
                
                InputField(inputvalue = inputvalue)
            }
        }
    }

    @Composable
    fun InputField(inputvalue: MutableState<TextFieldValue>){
        Column(
            Modifier
                .fillMaxWidth()
        ) {

            TextField(
                value = inputvalue.value,
                onValueChange = {
                    inputvalue.value = it
                },
                placeholder = { Text(text = "Enter your message") },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    autoCorrect = true,
                    keyboardType = KeyboardType.Text,
                    imeAction = androidx.compose.ui.text.input.ImeAction.Done
                ),
                textStyle = TextStyle(
                    color = Color.Black, fontSize = TextUnit.Unspecified,
                    fontFamily = FontFamily.SansSerif
                ),
                maxLines = 1,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    if (inputvalue.value.text.isNotEmpty()) {
                        ChatServer.sendMessage(inputvalue.value.text)
                        inputvalue.value = TextFieldValue()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Send", fontSize = 15.sp)
            }
        }
    }
    
    @Composable
    fun ChatsList(messagesList: List<Message>) {
        LazyColumn(modifier = Modifier.background(Color.White)) {
            items(count = messagesList.size) { index ->
                if (messagesList.isNotEmpty())
                    ShowChat(message = messagesList[index])
            }
        }
    }

}