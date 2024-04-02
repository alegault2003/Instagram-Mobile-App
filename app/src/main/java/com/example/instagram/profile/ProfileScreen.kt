package com.example.instagram.profile

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.instagram.Account
import com.example.instagram.BottomBar
import com.example.instagram.IconButton
import com.example.instagram.largeIcon
import com.example.instagram.midIcon
import com.example.instagram.navigation.NavigationDestination
import com.example.instagram.AppViewModelProvider
import com.example.instagram.ContactsViewModel
import com.example.instagram.Post
import com.example.instagram.findUser
import com.google.firebase.firestore.FirebaseFirestore

object ProfileDestination : NavigationDestination {
    override val route = "Profile"
    override val titleRes = "Info"
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}

class ProfileViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val userInfo = checkNotNull(savedStateHandle[ProfileDestination.itemIdArg]) as String
}

@Composable
fun ProfileScreen(
    navigateToPersonalProfile: () -> Unit,
    navigateToFeed: () -> Unit,
    navigateToChat: (String) -> Unit,
    profileViewModel: ProfileViewModel = viewModel(factory = AppViewModelProvider.Factory),
    contactsViewModel: ContactsViewModel,
    db: FirebaseFirestore
){
    Log.d("USER LIST", contactsViewModel.userList.value.toString())

    val currentUser = contactsViewModel.currentUser
    val user = findUser(profileViewModel.userInfo, contactsViewModel)!!

    var posts = remember { mutableStateOf( user.posts.size ) }
    var followers = remember { mutableStateOf( user.followers ) }
    var following = remember { mutableStateOf( user.following ) }

    var currentFollowing = remember { mutableStateOf(currentUser.following) }

    Column {
        ProfileTopBar(user)

        ProfileInfo(posts, followers, following)

        FollowButtons(user, followers, db, currentUser, currentFollowing, navigateToChat)

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
val space = 2

@Composable
fun Posts(user: Account){
    Text(
        text = "Posts",
        modifier = Modifier.fillMaxWidth(),
        style = TextStyle(fontSize = 20.sp),
        textAlign = TextAlign.Center
    )
    Spacer(Modifier.height(5.dp))
    Divider(Modifier.fillMaxWidth())

    PostsRow(user)
}

@Composable
fun PostsRow(user: Account) {
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(space.dp)
    ) {
        ProfileSmallPost(user.posts[0])
        Spacer(modifier = Modifier.width(space.dp))
//        ProfileSmallPost(user.posts)
//        Spacer(modifier = Modifier.width(space.dp))
//        ProfileSmallPost(user.posts)
    }
}

@Composable
fun ProfileSmallPost(post: Post) {
    val config = LocalConfiguration.current
    val screenWidth = config.screenWidthDp

    val photoSize = screenWidth / 3 - (space * 2)

    Image(
        painter = painterResource(post.id),
        contentDescription = "Post",
        modifier = Modifier
            .size(photoSize.dp)
            .background(Color.Black)
    )
}

@Composable
fun FollowButtons(
    user: Account,
    followers: MutableState<Int>,
    db: FirebaseFirestore,
    currentUser: Account,
    currentFollowing: MutableState<Int>,
    navigateToChat: (String) -> Unit
){
    var text by remember { mutableStateOf("Follow") }
    var color by remember { mutableStateOf(Color.Cyan) }
    var change by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.padding(10.dp)
    ){
        Button(
            onClick = {
                if(!change) {
                    text = "Following"
                    color = Color.LightGray
                    change = true
                    followers.value += 1
                    //currentFollowing.value += 1
                }else{
                    text = "Follow"
                    color = Color.Cyan
                    change = false
                    followers.value -= 1
                    //currentFollowing.value -= 1
                }
                val info = hashMapOf(
                    "username" to user.username,
                    "followers" to followers.value,
                    "following" to user.following
                )
                db.collection(user.username).document("user info").set(info)

                val currentInfo = hashMapOf(
                    "username" to currentUser.username,
                    "followers" to currentUser.followers,
                    "following" to currentFollowing.value
                )
                db.collection(currentUser.username).document("my user info").set(currentInfo)
            },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(5.dp),
            colors = ButtonDefaults.buttonColors(color)
        ) {
            Text(
                text = text,
                modifier = Modifier.fillMaxWidth(),
                color = Color.Black,
                textAlign = TextAlign.Center,
                style = TextStyle(fontSize = 20.sp)
            )
        }
        Spacer(Modifier.width(5.dp))
        Button(
            onClick = { navigateToChat(user.username) },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(5.dp),
            colors = ButtonDefaults.buttonColors(Color.LightGray)
        ) {
            Text(
                text = "Message",
                modifier = Modifier.fillMaxWidth(),
                color = Color.Black,
                textAlign = TextAlign.Center,
                style = TextStyle(fontSize = 20.sp)
            )
        }
    }
}

@Composable
fun ProfileInfo(posts: MutableState<Int>, followers: MutableState<Int>, following: MutableState<Int>){
    Row(
        modifier = Modifier.padding(10.dp)
    ) {
        Column {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Account story",
                tint = Color.LightGray,
                modifier = Modifier.size(largeIcon.dp)
            )
            Text(
                text = "Your story",
                modifier = Modifier.width(80.dp),
                textAlign = TextAlign.Center,
                style = TextStyle(fontSize = 15.sp)
            )
        }
        Spacer(Modifier.width(5.dp))
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            InfoText(info = posts.value.toString(), title = "Posts")
            InfoText(info = followers.value.toString(), title = "Followers")
            InfoText(info = following.value.toString(), title = "Following")
        }
    }
}

@Composable
fun RowScope.InfoText(info: String, title: String){
    Column (
        modifier = Modifier
            .height(largeIcon.dp)
            .weight(1f),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(
            text = info,
            style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Black)
        )
        Text(
            text = title,
            style = TextStyle(fontSize = 17.sp)
        )
    }
}

@Composable
fun ProfileTopBar(user: Account?) {
    Row (
        modifier = Modifier
            .padding(15.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Text(
            text = user!!.username,
            style = TextStyle(fontSize = 35.sp, fontWeight = FontWeight.Black)
        )
        IconButton(icon = Icons.Default.List, size = midIcon, onClick = {})
    }
}

