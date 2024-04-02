package com.example.instagram

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.instagram.navigation.InstaNavHost
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun Instagram(
    db: FirebaseFirestore,
    viewModel: ContactsViewModel,
    navController: NavHostController = rememberNavController()
){
    InstaNavHost(navController = navController, db, viewModel)
}

val iconSize = 30
val midIcon = 40
val feedPadding = 10
val largeIcon = 80

data class Message(val author: String, val body: String, val time: String)

data class Account(val username: String, val followers: Int, val following: Int, val posts: ArrayList<Post>)

data class Contact(val id: Long, val name: String)

data class Post(val id: Int, val likes: Int, val comment: String, val docName: String)

var contacts = arrayOf(
    Contact(0, "Agathe Legault"),
    Contact(1, "John Doe"),
    Contact(2, "Mary Smith"),
    Contact(3, "Cynthia Clarke"),
    Contact(4, "Poppy Balfour"),
    Contact(5, "Cassian"),
    Contact(6, "Aaron Warner"),
    Contact(7, "Rachel August"),
    Contact(8, "Sierra Horne"),
    Contact(9, "Seraphena Mierel"),
    Contact(10, "Bea Rouse"),
    Contact(11, "Erin Deyell"),
    Contact(12, "Samuel Grant"),
    Contact(13, "Chantale Boudreault"),
    Contact(14, "Judith Tremblay"),
)

var comments = arrayOf(
    "Thought this was so funny",
    "Does anyone else totally relate to this?",
    "Me when...",
    "how i feel when i wake up",
    "**SPOILER**",
    "studying makes me feel this way",
    "felt cute, might delete later",
    "this makes me think of my best friend :)",
    "this drama is getting annoying",
    "im the ITGIRL. you know i am that girl",
    "when the book makes me mad",
    "bestie looking cute",
    "watching a movie later",
    "can't wait for break",
    "Merry Chrysler"
)

var images = arrayOf(
    R.raw.dog, R.raw.coughing_cat, R.raw.demon_slayer, R.raw.fish_hat, R.raw.heebies_jeebies, R.raw.handsome_squidward, R.raw.injured_woman,
    R.raw.josh_hutcherson, R.raw.shaving_cream, R.raw.shrek_wazowski, R.raw.side_eye, R.raw.spongebob, R.raw.tengen, R.raw.walrus,
    R.raw.weird_look
)

@Composable
fun CreateDatabase(db: FirebaseFirestore, viewModel: ContactsViewModel, currentUser: String) {

    db.collection(currentUser).get().addOnSuccessListener { result ->
        var found = false
        for (data in result) {
            if (data.id == "my user info") found = true
        }
        if (found) {
            var name = "";
            var followers: Long = 0;
            var following: Long = 0
            var allPosts = arrayListOf<Post>()

            for (document in result) {
                if (document.id == "my user info") {
                    name = document.data["username"] as String
                    followers = document.data["followers"] as Long
                    following = document.data["following"] as Long
                } else {
                    Log.d("DOC ID", document.id)
                    var id = document.data["post"] as Long
                    var likes = document.data["likes"] as Long
                    var comment = document.data["comment"] as String
                    var docName = document.id
                    var post = Post(
                        id = id.toInt(),
                        likes = likes.toInt(),
                        comment = comment,
                        docName = docName
                    )
                    allPosts.add(post)
                }

            }
            var user = Account(
                username = name,
                followers = followers.toInt(),
                following = following.toInt(),
                posts = allPosts
            )
            viewModel.currentUser = user
        }
        if (!found) {
            val info = hashMapOf(
                "username" to currentUser,
                "followers" to 0,
                "following" to 0
            )
            db.collection(currentUser).document("my user info").set(info)

            var rand = (0..images.size - 1).random()
            val post = hashMapOf(
                "post" to images[(0..images.size - 1).random()],
                "likes" to 0,
                "comment" to comments[(0..comments.size - 1).random()]
            )
            db.collection(currentUser).add(post)

            db.collection(currentUser).get().addOnSuccessListener { result ->
                for (data in result) {
                    if (data.id != "my user info") {
                        var docName = data.id

                        var userPost = Post(
                            id = post.get("post") as Int,
                            likes = post.get("likes") as Int,
                            comment = post.get("comment").toString(),
                            docName = docName
                        )

                        val allPosts = arrayListOf<Post>(userPost)
                        var user = Account(
                            username = info.get("username").toString(),
                            followers = info.get("followers") as Int,
                            following = info.get("following") as Int,
                            posts = allPosts
                        )

                        viewModel.currentUser = user
                    }

                }

            }
        }
    }

    for (item in contacts) {
        db.collection(item.name).get().addOnSuccessListener { result ->
            var found = false
            var messaged = false
            for (data in result) {
                if (data.id == "user info") found = true
                if (data.id == "messages") messaged = true
            }
            if (found) {
                var name = "";
                var followers: Long = 0;
                var following: Long = 0
                var allPosts = arrayListOf<Post>()

                for (document in result) {
                    if (document.id == "user info") {
                        name = document.data["username"] as String
                        followers = document.data["followers"] as Long
                        following = document.data["following"] as Long
                    } else if(document.id != "messages") {
                        var id = document.data["post"] as Long
                        var likes = document.data["likes"] as Long
                        var comment = document.data["comment"] as String
                        var docName = document.id
                        var post = Post(
                            id = id.toInt(),
                            likes = likes.toInt(),
                            comment = comment,
                            docName = docName
                        )
                        allPosts.add(post)
                    }

                }
                var user = Account(
                    username = name,
                    followers = followers.toInt(),
                    following = following.toInt(),
                    posts = allPosts
                )
                viewModel.userList.value = (viewModel.userList.value ?: mutableListOf()).toMutableList().apply { add(user) }
                if(messaged){
                    viewModel.messageUsers.value = (viewModel.messageUsers.value ?: mutableListOf()).toMutableList().apply{ add(user) }
                }
            }

            if (!found) {
                val info = hashMapOf(
                    "username" to item.name,
                    "followers" to 0,
                    "following" to 0
                )
                db.collection(item.name).document("user info").set(info)

                var rand = (0..images.size - 1).random()
                val post = hashMapOf(
                    "post" to images[(0..images.size - 1).random()],
                    "likes" to 0,
                    "comment" to comments[(0..comments.size - 1).random()]
                )
                db.collection(item.name).add(post)

                db.collection(item.name).get().addOnSuccessListener { result ->
                    for (data in result) {
                        if (data.id != "user info") {
                            var docName = data.id

                            var userPost = Post(
                                id = post.get("post") as Int,
                                likes = post.get("likes") as Int,
                                comment = post.get("comment").toString(),
                                docName = docName
                            )

                            val allPosts = arrayListOf<Post>(userPost)
                            var user = Account(
                                username = info.get("username").toString(),
                                followers = info.get("followers") as Int,
                                following = info.get("following") as Int,
                                posts = allPosts
                            )
                            viewModel.userList.value = (viewModel.userList.value
                                ?: mutableListOf()).toMutableList().apply { add(user) }
                        }

                    }

                }
            }
        }
    }
    var list = viewModel.messageUsers.value!!
    Log.d("MESSAGE USERS", list.toString())
}

fun findUser(name: String, viewModel: ContactsViewModel): Account? {
    val users = viewModel.userList.value

    if (users != null) {
        for (user in users){
            if (user.username == name){
                return user
            }
        }
    }
    return null
}

@Composable
fun BottomBar(toPersonalProfile: () -> Unit, toFeed: () -> Unit){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(feedPadding.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ){
        IconButton(icon = Icons.Default.Home, size = iconSize, onClick = toFeed)
        IconButton(icon = Icons.Default.Search, size = iconSize, onClick = {})
        IconButton(icon = Icons.Default.AddCircleOutline, size = iconSize, onClick = {})

        AsyncImage(
            model = "https://seeklogo.com/images/I/instagram-reels-logo-18CF7D9510-seeklogo.com.png",
            contentDescription = "Message Logo",
            modifier = Modifier.size(iconSize.dp)
        )

        IconButton(icon = Icons.Default.AccountCircle, size = iconSize, onClick = toPersonalProfile)
    }
}

@Composable
fun IconButton(icon: ImageVector, size: Int, onClick: () -> Unit){
    Icon(
        imageVector = icon,
        contentDescription = "Account",
        modifier = Modifier
            .size(size.dp)
            .clickable(
                onClick = onClick
            )
    )
}

@Composable
fun ActionButton(size: Int, icon: ImageVector, onClick: () -> Unit){
    FloatingActionButton(
        onClick = onClick,
        modifier = Modifier
            .width(size.dp)
            .height(size.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "Back to List",
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputField(text: MutableState<String>, label: String){
    val config = LocalConfiguration.current
    val screenWidth = config.screenWidthDp

    val inputLength = screenWidth / 10 * 9

    OutlinedTextField(
        value = text.value,
        onValueChange = {text.value = it},
        label = {
            Text(
                text = label,
                color = Color.Gray,
                style = TextStyle(fontSize = 15.sp),
            )
        },
        textStyle = TextStyle(fontSize = 15.sp),
        shape = RoundedCornerShape(15.dp),
        colors = TextFieldDefaults.textFieldColors(containerColor = Color.LightGray),
        modifier = Modifier
            .width(inputLength.dp)
            .height(55.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordField(text: MutableState<String>, label: String){
    val config = LocalConfiguration.current
    val screenWidth = config.screenWidthDp

    val inputLength = screenWidth / 10 * 9

    OutlinedTextField(
        value = text.value,
        onValueChange = { text.value = it },
        label = {
            Text(
                text = label,
                color = Color.Gray,
                style = TextStyle(fontSize = 15.sp)
            )
        },
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        textStyle = TextStyle(fontSize = 15.sp),
        shape = RoundedCornerShape(15.dp),
        colors = TextFieldDefaults.textFieldColors(containerColor = Color.LightGray),
        modifier = Modifier
            .width(inputLength.dp)
            .height(55.dp)
    )
}

@Composable
fun EnterButton(text: String, onClick: () -> Unit){
    val config = LocalConfiguration.current
    val screenWidth = config.screenWidthDp

    val inputLength = screenWidth / 10 * 9

    Button(
        onClick = onClick,
        shape = RoundedCornerShape(35.dp),
        modifier = Modifier
            .width(inputLength.dp)
    ) {
        Text(
            text = text,
            style = TextStyle(fontSize = 20.sp),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}