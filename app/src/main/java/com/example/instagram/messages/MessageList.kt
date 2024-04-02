package com.example.instagram.messages

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.instagram.ActionButton
import com.example.instagram.ContactsViewModel
import com.example.instagram.Feed.PersonalStory
import com.example.instagram.Feed.Story
import com.example.instagram.IconButton
import com.example.instagram.InputField
import com.example.instagram.feedPadding
import com.example.instagram.midIcon
import com.example.instagram.navigation.NavigationDestination
import androidx.compose.foundation.lazy.items
import com.example.instagram.Account
import com.google.firebase.firestore.FirebaseFirestore

object MessageListDestination : NavigationDestination {
    override val route = "Messages"
    override val titleRes = "Message List"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageList(
    navigateBack: () -> Unit,
    navigateToNew: () -> Unit,
    navigateToChat: (String) -> Unit,
    viewModel: ContactsViewModel,
    db: FirebaseFirestore
){
    Log.d("DEBUG", "in message list")
    var text = remember { mutableStateOf("") }
    Column {
        TopBar(navigateBack, navigateToNew)

        Spacer(Modifier.height(5.dp))

        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(feedPadding.dp),
            horizontalArrangement = Arrangement.Center
        ){
            InputField(text = text, label = "Search")
        }

        Notes()

        Messages(navigateToChat, viewModel)
    }
}

@Composable
fun Messages(toChat: (String) -> Unit, viewModel: ContactsViewModel){
    val contacts by viewModel.messageUsers.observeAsState()

    Row(
        modifier = Modifier
            .padding(15.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Text(
            text = "Messages",
            style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Black)
        )
        Text(
            text = "Requests",
            style = TextStyle(fontSize = 20.sp),
            color = Color.Cyan
        )
    }

    var totalUsers = arrayListOf<String>()

    LazyColumn(){
        items(contacts!!){ user ->
            Log.d("MESSAGE ITEM", contacts!!.size.toString())
            if (!inTotalUsers(user.username, totalUsers)) {
                totalUsers.add(user.username)
                MessageItem(toChat, user)
            }
        }
    }

}

fun inTotalUsers(name: String, totalUsers: ArrayList<String>): Boolean {
    var found = false
    for (user in totalUsers){
        if (user == name) found = true
    }
    return found
}


@Composable
fun MessageItem(toChat: (String) -> Unit, user: Account){
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = {toChat(user.username)}
            )
    ) {
        Row {
            Spacer(Modifier.width(15.dp))
            IconButton(icon = Icons.Default.AccountCircle, size = 50, onClick = {})

            Spacer(Modifier.width(10.dp))

            Column (
                modifier = Modifier.height(50.dp),
                verticalArrangement = Arrangement.Center
            ){
                Text(
                    text = user.username,
                    style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
                )
            }
        }


        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            color = Color.Gray,
            thickness = 1.dp
        )
    }
}

@Composable
fun Notes(){
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(feedPadding.dp)
    ){
        PersonalStory(text = "Your Note")
        Story()
        Story()
        Story()
        Story()
        Story()
    }
}

@Composable
fun TopBar(back: () -> Unit, toNew: () -> Unit){
    Row (
        modifier = Modifier
            .padding(feedPadding.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Row{
            ActionButton(size = midIcon, icon = Icons.Default.ArrowBack, onClick = back)

            Spacer(Modifier.width(10.dp))

            Text(
                text = "Username",
                style = TextStyle(fontSize = 35.sp, fontWeight = FontWeight.Black)
            )
        }

        Row{
            IconButton(icon = Icons.Default.VideoCall, size = midIcon, onClick = {})

            Spacer(modifier = Modifier.width(5.dp))

            IconButton(icon = Icons.Default.Add, size = midIcon, onClick = toNew)
        }
    }
}

//if(messaged){
//    var count = 0
//    var finish = false
//    for (document in result){
//        if(document.id == "messages"){
//            while (!finish){
//                var str = "message" + count
//                if (document.data[str] != null){
//
//                }
//            }
//        }
//    }
//}