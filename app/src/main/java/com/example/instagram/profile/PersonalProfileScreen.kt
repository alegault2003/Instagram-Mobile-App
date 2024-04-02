package com.example.instagram.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.instagram.BottomBar
import com.example.instagram.ContactsViewModel
import com.example.instagram.navigation.NavigationDestination

object PersonalProfileDestination : NavigationDestination {
    override val route = "Personal Profile"
    override val titleRes = "Personal Info"
}

@Composable
fun PersonalProfileScreen(
    navigateToPersonalProfile: () -> Unit,
    navigateToFeed: () -> Unit,
    viewModel: ContactsViewModel
){
    val user = viewModel.currentUser
    var posts = remember { mutableStateOf( user.posts.size ) }
    var followers = remember { mutableStateOf( user.followers ) }
    var following = remember { mutableStateOf( user.following ) }

    Column {
        ProfileTopBar(user)

        ProfileInfo(posts, followers, following)

        EditButton()

        Spacer(Modifier.height(10.dp))

        Posts(user)
        Column (
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom
        ){
            BottomBar(navigateToPersonalProfile, navigateToFeed)
        }
    }
}

@Composable
fun EditButton(){
    Row(
        modifier = Modifier.padding(10.dp)
    ){
        Button(
            onClick = { /*TODO*/ },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(5.dp),
            colors = ButtonDefaults.buttonColors(Color.LightGray)
        ) {
            Text(
                text = "Edit Picture",
                modifier = Modifier.fillMaxWidth(),
                color = Color.Black,
                textAlign = TextAlign.Center,
                style = TextStyle(fontSize = 20.sp)
            )
        }
        Spacer(Modifier.width(5.dp))
        Spacer(Modifier.width(5.dp).weight(1f))
    }
}