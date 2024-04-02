package com.example.instagram.Feed

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.example.instagram.navigation.NavigationDestination
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.instagram.BottomBar
import com.example.instagram.ContactsViewModel
import com.example.instagram.CreateDatabase
import com.example.instagram.IconButton
import com.example.instagram.feedPadding
import com.example.instagram.iconSize
import com.example.instagram.largeIcon
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ButtonDefaults
import com.example.instagram.Account

object FeedDestination : NavigationDestination {
    override val route = "Feed"
    override val titleRes = "Posts"
}


@Composable
fun FeedScreen(
    navigateToMessages: () -> Unit,
    //navigateToAddStory: () -> Unit,
    //navigateToNewPost: () -> Unit,
    navigateToProfile: (String) -> Unit,
    navigateToPersonalProfile: () -> Unit,
    db: FirebaseFirestore,
    viewModel: ContactsViewModel,
){
    val currentUser = viewModel.accountName
    Log.d("FEED", "in feed screen")
    Column {
        FeedTopBar(navigateToMessages)

        Column (
            modifier = Modifier
                .weight(weight = 1f, fill = false)
        ){

            //set up parameter for post username
            AllPosts(navigateToProfile, db, viewModel, currentUser)
        }
        BottomBar(navigateToPersonalProfile, {})
    }
}

@Composable
fun FeedTopBar(toMessages: () -> Unit){
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Text(
            text = "Instagram",
            fontFamily = FontFamily.Cursive,
            fontSize = 40.sp
        )
        Row {
            IconButton(icon = Icons.Default.FavoriteBorder, size = 40, onClick = {})

            Spacer(Modifier.width(5.dp))

            AsyncImage(
                model = "https://static.thenounproject.com/png/4805005-200.png",
                contentDescription = "Message Logo",
                modifier = Modifier
                    .size(40.dp)
                    .clickable(
                        onClick = toMessages
                    )
            )
        }
    }
}

@Composable
fun AllStories(){
    Row (
        Modifier
            .padding(feedPadding.dp)
            .horizontalScroll(rememberScrollState())) {
        PersonalStory("Your Story")

        //add parameters to story for usernames
        Story()
        Story()
        Story()
        Story()
        Story()
    }

    Spacer(modifier = Modifier.height(10.dp))
}

@Composable
fun PersonalStory(text: String){
    Column {
        BoxWithConstraints {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Account story",
                tint = Color.LightGray,
                modifier = Modifier.size(largeIcon.dp)
            )
            Row() {
                Spacer(modifier = Modifier.width(5.dp))
                Column {
                    Spacer(modifier = Modifier.height(5.dp))
                    Icon(
                        imageVector = Icons.Default.AddCircle,
                        contentDescription = text,
                        tint = Color.Blue,
                        modifier = Modifier.zIndex(2f)
                    )
                }
            }
        }

        Text(
            text = "Your story",
            modifier = Modifier.width(80.dp),
            textAlign = TextAlign.Center,
            style = TextStyle(fontSize = 15.sp)
        )
    }
}

@Composable
fun Story(){
    Column {
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "Account story",
            tint = Color.LightGray,
            modifier = Modifier.size(largeIcon.dp)
        )
        Text(
            text = "Story",
            modifier = Modifier.width(80.dp),
            textAlign = TextAlign.Center,
            style = TextStyle(fontSize = 15.sp)
        )
    }
}

@Composable
fun AllPosts(
    toProfile: (String) -> Unit,
    db: FirebaseFirestore,
    viewModel: ContactsViewModel,
    currentUser: String
){

    CreateDatabase(db = db, viewModel = viewModel, currentUser)
    val userList by viewModel.userList.observeAsState()
    val suggest = arrayOf(1)

    LazyColumn(){
        items(suggest) {
            AllStories()
            SuggestedFollows(toProfile, viewModel, db)
        }
        items(userList!!) { user ->
            Post(toProfile, user, db)
        }
    }
}

@Composable
fun Post(toProfile: (String) -> Unit, user: Account, db: FirebaseFirestore){
    Log.d("USER POSTS", user.posts.size.toString())
    val config = LocalConfiguration.current
    val screenWidth = config.screenWidthDp

    val numPosts = user.posts.size

    if(numPosts > 0) {
        Box {
            Column {
                PostTopBar(toProfile, user)

                Image(
                    painter = painterResource(user.posts.get(numPosts - 1).id!!.toInt()),
                    contentDescription = "Post",
                    modifier = Modifier
                        .size(screenWidth.dp)
                        .background(Color.Black)
                )

                PostInteractive(user, db)
            }
        }
    }
}

@Composable
fun PostTopBar(toProfile: (String) -> Unit, user: Account){
    Log.d("USER", user.toString())
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(feedPadding.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Row {
            IconButton(icon = Icons.Default.AccountCircle, size = 40, onClick = { toProfile(user.username) })

            Spacer(modifier = Modifier.width(5.dp))

            Column {
                Text(
                    text = user.username,
                    style = TextStyle(fontSize = 17.sp, fontWeight = FontWeight.Black)
                )

                Text(text = "Location")
            }
        }
        IconButton(icon = Icons.Default.MoreVert, size = iconSize, onClick = {})
    }
}

@Composable
fun PostInteractive(user: Account, db: FirebaseFirestore){
    var liked by remember { mutableStateOf(Icons.Default.FavoriteBorder) }
    var numLikes by remember { mutableStateOf(user.posts[0].likes) }
    var change by remember { mutableStateOf(false) }

    Column (modifier = Modifier.padding(feedPadding.dp)){
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row {
                // TODO: change to filled heart when clicked
                IconButton(icon = liked, size = iconSize){
                    if(!change) {
                        liked = Icons.Default.Favorite
                        numLikes += 1
                        change = true
                    }else {
                        liked = Icons.Default.FavoriteBorder
                        numLikes -= 1
                        change = false
                    }
                    val post = hashMapOf(
                        "post" to user.posts[0].id,
                        "likes" to numLikes,
                        "comment" to user.posts[0].comment
                    )
                    db.collection(user.username).document(user.posts[0].docName).set(post)

                }

                Spacer(modifier = Modifier.width(5.dp))
                AsyncImage(
                    model = "https://cdn0.iconfinder.com/data/icons/social-media-logo-4/32/Social_Media_instagram_comment-512.png",
                    contentDescription = "Message Logo",
                    modifier = Modifier.size(iconSize.dp)
                )

                Spacer(modifier = Modifier.width(5.dp))
                IconButton(icon = Icons.Default.Send, size = iconSize, onClick = {})
            }
            IconButton(icon = Icons.Default.BookmarkBorder, size = iconSize, onClick = {})
        }

        Column (modifier = Modifier.padding(5.dp)){
            Text(
                text = "$numLikes Likes",
                style = TextStyle(fontSize = 17.sp, fontWeight = FontWeight.Black)
            )
            Text(
                text = user.username,
                style = TextStyle(fontSize = 17.sp, fontWeight = FontWeight.Black)
            )
            Text(
                text = user.posts[0].comment
            )
        }
    }
}

@Composable
fun SuggestedFollows(toProfile: (String) -> Unit, viewModel: ContactsViewModel, db: FirebaseFirestore){
    var users = viewModel.userList.value
    Column (
        modifier = Modifier
            .background(Color.LightGray)
            .padding(10.dp)
    ){
        Text(
            text = "Suggested for You",
            style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold)
        )
        Row (
            modifier = Modifier.horizontalScroll(rememberScrollState())
        ){
            repeat(users!!.size){
                ProfileBoxes(toProfile, users[(0..users.size-1).random()], viewModel, db)
            }
        }
    }
}

@Composable
fun ProfileBoxes(onClick: (String) -> Unit, user: Account, viewModel: ContactsViewModel, db: FirebaseFirestore){
    var currentUser = viewModel.currentUser

    var text by remember { mutableStateOf("Follow") }
    var color by remember { mutableStateOf(Color.Cyan) }
    var change by remember { mutableStateOf(false) }
    var followers = remember { mutableStateOf(user.followers) }
    var following = remember { mutableStateOf(currentUser.following) }

    Box (
        modifier = Modifier
            .size(width = 185.dp, height = 250.dp)
            .padding(5.dp)
            .background(Color.White)
            .clickable(
                onClick = { onClick(user.username) }
            )
    ){
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(5.dp)
        ){
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Account story",
                tint = Color.LightGray,
                modifier = Modifier.size(150.dp)
            )
            Text(
                text = user.username,
                style = TextStyle(fontSize = 17.sp, fontWeight = FontWeight.Black)
            )
            Spacer(Modifier.height(5.dp))

            //TODO: change button to grey and "following" when clicked
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
                        "following" to following.value
                    )
                    db.collection(currentUser.username).document("my user info").set(currentInfo)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(color)
            ) {
                Text(
                    text = text,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = TextStyle(fontSize = 17.sp),
                    color = Color.Black
                )
            }
        }
    }
}
