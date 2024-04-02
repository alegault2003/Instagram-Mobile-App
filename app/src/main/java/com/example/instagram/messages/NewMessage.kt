package com.example.instagram.messages

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.example.instagram.ActionButton
import com.example.instagram.midIcon
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import com.example.instagram.ContactsViewModel
import com.example.instagram.EnterButton
import com.example.instagram.contacts
import com.example.instagram.feedPadding
import com.example.instagram.navigation.NavigationDestination
import com.google.firebase.firestore.FirebaseFirestore

object NewMessageDestination : NavigationDestination {
    override val route = "New Message"
    override val titleRes = "Posts"
}

@Composable
fun NewMessage(
    navigateBack: () -> Unit,
    navigateToChat: (String) -> Unit,
    viewModel: ContactsViewModel,
    db: FirebaseFirestore
){
    var selectedUser = remember { mutableStateOf("") }

    Column {
        TopBar(navigateBack)

        Recipient(selectedUser, viewModel)

        Column (
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            EnterButton(text = "Create Chat"){
                var dummyMap = hashMapOf<String, Object>()
                db.collection(selectedUser.value).document("messages").set(dummyMap)
                navigateToChat(selectedUser.value)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Recipient(selectedUser: MutableState<String>, viewModel: ContactsViewModel) {
    val options = createList(viewModel = viewModel)
    var expanded by remember { mutableStateOf(false) }
    var textfieldSize by remember { mutableStateOf(Size.Zero)}

    val icon = if (expanded)
        Icons.Filled.ArrowDropUp //it requires androidx.compose.material:material-icons-extended
    else
        Icons.Filled.ArrowDropDown

    Row (
        modifier = Modifier.padding(15.dp),
        verticalAlignment = Alignment.CenterVertically
    ){
        Text(
            text = "To:",
            style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Black)
        )

        Spacer(Modifier.width(10.dp))

        Box() {
            OutlinedTextField(
                value = selectedUser.value,
                onValueChange = { selectedUser.value = it },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        //This value is used to assign to the DropDown the same width
                        textfieldSize = coordinates.size.toSize()
                    },
                label = {Text("Select")},
                trailingIcon = {
                    Icon(icon,"contentDescription",
                        Modifier.clickable (
                            onClick = {expanded = !expanded }
                        ) )
                }
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .width(with(LocalDensity.current){textfieldSize.width.toDp()})
            ) {
                options.forEach { label ->
                    DropdownMenuItem(
                        text = { Text(text = label) },
                        onClick = {
                            selectedUser.value = label
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TopBar(back: () -> Unit){
    Row (
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(feedPadding.dp)
    ){
        ActionButton(size = midIcon, icon = Icons.Default.ArrowBack, onClick = back)

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = "New Message",
            style = TextStyle(fontSize = 25.sp, fontWeight = FontWeight.Black)
        )
    }
}

@Composable
fun createList(viewModel: ContactsViewModel): ArrayList<String> {
    var size = viewModel.userList.value!!.size

    val users by viewModel.userList.observeAsState()
    Log.d("CREATING LIST", users.toString())
    var list = arrayListOf<String>()
    for (i in (0..contacts.size-1)){
        users?.get(i)?.let { list.add(it.username) }
    }
    return list
}