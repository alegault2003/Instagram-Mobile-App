package com.example.instagram.messages

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.instagram.Account
import com.example.instagram.ActionButton
import com.example.instagram.AppViewModelProvider
import com.example.instagram.ContactsViewModel
import com.example.instagram.Message
import com.example.instagram.findUser
import com.example.instagram.midIcon
import com.example.instagram.navigation.NavigationDestination
import com.example.instagram.profile.ProfileDestination
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date

object ChatDestination : NavigationDestination {
    override val route = "Chats"
    override val titleRes = "Chat Screen"
    const val itemIdArg = "itemId"
    val routeWithArgs = "${route}/{$itemIdArg}"
}

class ChatViewModel(
    savedStateHandle: SavedStateHandle
): ViewModel() {
    val userInfo = checkNotNull(savedStateHandle[ProfileDestination.itemIdArg]) as String
}

val padding = 10
val inputHeight = 50
val space = 5
val topbarHeight = 40

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navigateBack: () -> Unit,
    chatViewModel: ChatViewModel = viewModel(factory = AppViewModelProvider.Factory),
    viewModel: ContactsViewModel,
    db: FirebaseFirestore
){
    val name = chatViewModel.userInfo
    val messageUser = findUser(name = name, viewModel = viewModel)!!

    val config = LocalConfiguration.current
    val screenWidth = config.screenWidthDp

    val totalWidth = screenWidth - (padding * 2)
    val inputLength = totalWidth - inputHeight - space

    var text by remember { mutableStateOf("") }
    var clickedText by remember { mutableStateOf("") }

    val keyboardHeight = WindowInsets.ime.getBottom(LocalDensity.current)
    val coroutineScope = rememberCoroutineScope()
    var scrollState = rememberScrollState()

    LaunchedEffect(key1 = keyboardHeight){
        coroutineScope.launch {
            scrollState.scrollBy(keyboardHeight.toFloat())
        }
    }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ){
        Row (Modifier.fillMaxWidth()) {
            ActionButton(size = midIcon, icon = Icons.Default.ArrowBack, onClick = navigateBack)

            Spacer(Modifier.width(5.dp))

            Text(
                text = messageUser.username,
                style = TextStyle(fontSize = 35.sp, fontWeight = FontWeight.Black)
            )
        }

        Spacer(Modifier.height(space.dp))

        Column (
            Modifier.verticalScroll(scrollState)
        ){
            //TODO: add messages
            ChatList(messageUser, db, viewModel)

            Spacer(Modifier.height(space.dp))

            Row(
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier
                    .fillMaxSize()
            ){
                OutlinedTextField(
                    value = text,
                    onValueChange = {
                        text = it
                    },
                    modifier = Modifier
                        .width(inputLength.dp)
                        .height(inputHeight.dp),
                    textStyle = TextStyle(fontSize = 18.sp),
                    shape = RoundedCornerShape(40.dp)
                )

                Spacer(Modifier.width(space.dp))

                ActionButton(
                    inputHeight,
                    Icons.Default.Send,
                    onClick = {
                        clickedText = text
                        viewModel.msgList.value = (viewModel.msgList.value?: mutableListOf()).toMutableList().apply{
                            clear()
                            add(Message("","", ""))
                        }
                        val sdf = SimpleDateFormat("HH:mm:ss")

                        val msg = hashMapOf(
                            "body" to clickedText,
                            "author" to "You",
                            "time" to sdf.format(Date())
                        )

                        var doc = db.collection(messageUser.username).document("messages")
                        doc.collection("messages").add(msg)
                            .addOnSuccessListener { documentReference ->
                                Log.d("TAG", "DocumentSnapshot written with ID: ${documentReference.id}")
                            }
                            .addOnFailureListener { e ->
                                Log.w("TAG", "Error adding document", e)
                            }

                        text = ""
                    }
                )
            }
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ChatList(messageUser: Account, db: FirebaseFirestore, viewModel: ContactsViewModel) {
    val config = LocalConfiguration.current
    val screenHeight = config.screenHeightDp

    var height = screenHeight - (padding * 2) - topbarHeight - space - inputHeight - space - 10

    CreateMessages(messageUser, db, viewModel)
    val messages by viewModel.msgList.observeAsState()

    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberLazyListState()

    coroutineScope.launch {
        if(viewModel.msgList.value!!.size != 0) {
            scrollState.animateScrollToItem(viewModel.msgList.value!!.size - 1)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .height(height.dp),
        state = scrollState,
        verticalArrangement = Arrangement.Bottom
    ) {
        items(messages!!) { msg ->
            sortMessages(viewModel = viewModel)
            if(msg.author != "") {
                MessageCard(msg = msg)
            }
        }
    }
}



@Composable
fun MessageCard(msg: Message){
    Spacer(
        Modifier
            .fillMaxWidth()
            .height(5.dp))

    if(msg.author != "You") {
        Row(Modifier.fillMaxWidth()) {
            Picture(length = 40)

            Spacer(modifier = Modifier.width(10.dp))

            TextInfo(msg = msg, false)
        }
    }else{
        Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
            TextInfo(msg = msg, true)

            Spacer(modifier = Modifier.width(10.dp))

            Picture(length = 40)
        }
    }

}

@Composable
fun TextInfo(msg: Message, alignText: Boolean){
    val config = LocalConfiguration.current
    val orien = config.orientation
    val screenWidth = config.screenWidthDp

    val maxWidth = screenWidth / 3 * 2
    Column {
        if(alignText) {
            Text(
                text = msg.author,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .widthIn(0.dp, maxWidth.dp)
                    .align(Alignment.End)
            )
        }else{
            Text(
                text = msg.author,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .widthIn(0.dp, maxWidth.dp)
                    .heightIn(0.dp, 100.dp)
            )
        }

        Spacer(modifier = Modifier.height(5.dp))

        Surface(shape = MaterialTheme.shapes.medium, shadowElevation = 10.dp) {
            if(alignText) {
                Text(
                    text = msg.body,
                    modifier = Modifier
                        .padding(all = 4.dp)
                        .widthIn(0.dp, maxWidth.dp),
                    //.background(Color.LightGray),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.End
                )
            }else{
                Text(
                    text = msg.body,
                    modifier = Modifier
                        .padding(all = 4.dp)
                        .widthIn(0.dp, maxWidth.dp),
                    //.background(Color.LightGray),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Start
                )
            }

        }
    }
}

@Composable
fun Picture(length: Int){
    Icon(
        imageVector = Icons.Default.AccountCircle,
        contentDescription = "profile picture",
        tint = Color.Gray,
        modifier = Modifier
            .width(length.dp)
            .height(length.dp)
    )
}

@Composable
fun CreateMessages(messageUser: Account, db: FirebaseFirestore, viewModel: ContactsViewModel) {

    db.collection(messageUser.username).get().addOnSuccessListener {result ->
        for (data in result){
            if(data.id == "messages"){
                db.collection(messageUser.username).document("messages").collection("messages").get().addOnSuccessListener {item ->
                    viewModel.msgList.value = (viewModel.msgList.value?: mutableListOf()).toMutableList().apply { clear() }
                    viewModel.msgList.value = (viewModel.msgList.value?: mutableListOf()).toMutableList().apply { add(Message("","","")) }
                    for (document in item){
                        var chat = Message(document.data["author"] as String, document.data["body"] as String, document.data["time"] as String)
                        viewModel.msgList.value = (viewModel.msgList.value?: mutableListOf()).toMutableList().apply { add(chat) }
                    }
                }
            }
        }
    }
}

fun sortMessages(viewModel: ContactsViewModel){
    var msgList = viewModel.msgList.value
    Log.d("DEBUG", msgList.toString())

    val n = msgList!!.size
    if(n > 1) {
        for (i in 0..(n-1)) {
            var swapped = false

            for(j in 0..(n-i-2)) {
                if(msgList[j].time > msgList[j+1].time){
                    var temp = msgList[j]
                    msgList[j] = msgList[j+1]
                    msgList[j+1] = temp
                    swapped = true
                }
            }
            if(!swapped){
                break
            }
        }
        Log.d("LOOP", "end of loop")
    }

    for (i in 1..msgList!!.size - 1) {
        Log.d("MESSAGES", msgList[i].toString())
    }
}