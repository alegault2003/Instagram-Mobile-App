package com.example.instagram.login

import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.instagram.ContactsViewModel
import com.example.instagram.EnterButton
import com.example.instagram.InputField
import com.example.instagram.PasswordField
import com.example.instagram.navigation.NavigationDestination
import kotlinx.coroutines.launch

object LoginDestination : NavigationDestination {
    override val route = "Login"
    override val titleRes = "Login"
}

val space = 10

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    navigateToCreate: () -> Unit,
    navigateToFeed: () -> Unit,
    viewModel: ContactsViewModel
) {
    val config = LocalConfiguration.current
    val orien = config.orientation
    val screenWidth = config.screenWidthDp
    val screenHeight = config.screenHeightDp
    val keyboardHeight = WindowInsets.ime.getBottom(LocalDensity.current)

    val inputLength = screenWidth / 10 * 9
    val sectionHeight = screenHeight / 3

    var username = remember { mutableStateOf("") }
    var password = remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    var scrollState = rememberScrollState()

    LaunchedEffect(key1 = keyboardHeight){
        coroutineScope.launch {
            scrollState.scrollBy(keyboardHeight.toFloat())
        }
    }

    Column(verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxSize()) {
        Column (verticalArrangement = Arrangement.Center, modifier = Modifier
            .fillMaxWidth()
            .weight(2f)
            //.height(sectionHeight.dp)
        ){
            //Spacer(Modifier.height(50.dp))
            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                AsyncImage(
                    model = "https://freelogopng.com/images/all_img/1658586823instagram-logo-transparent.png",
                    contentDescription = "Instagram Logo",
                    modifier = Modifier.size(75.dp)
                )
            }
        }

        Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically, modifier = Modifier
            .fillMaxWidth()
            .weight(3f)
            //.height(sectionHeight.dp)
        ) {
            Column {
                InputField(text = username , label = "Username or email")

                PasswordField(text = password, label = "Password")

                EnterButton(text = "Log In"){
                    if(username.value != "" && password.value != "") {
                        viewModel.accountName = username.value
                        navigateToFeed()
                    }
                }

            }
        }

        Column (modifier = Modifier
            .fillMaxWidth()
            .weight(2f),
            //.height(sectionHeight.dp),
            verticalArrangement = Arrangement.Bottom
        ){
            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = navigateToCreate,
                    shape = RoundedCornerShape(35.dp),
                    modifier = Modifier
                        .width(inputLength.dp)
                ) {
                    Text(
                        text = "Create Account",
                        style = TextStyle(fontSize = 20.sp),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }
            Spacer(Modifier.height(space.dp))
        }

    }
}
