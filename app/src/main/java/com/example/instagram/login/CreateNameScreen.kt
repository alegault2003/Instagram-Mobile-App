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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.instagram.navigation.NavigationDestination
import com.example.instagram.ActionButton
import com.example.instagram.ContactsViewModel
import com.example.instagram.EnterButton
import com.example.instagram.InputField
import kotlinx.coroutines.launch

object NameDestination : NavigationDestination {
    override val route = "New Account Name"
    override val titleRes = "Name"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateNameScreen(
    navigateToPassword: () -> Unit,
    navigateBack: () -> Unit,
    viewModel: ContactsViewModel
){
    val config = LocalConfiguration.current
    val screenWidth = config.screenWidthDp

    val inputLength = screenWidth / 10 * 9

    var name = remember { mutableStateOf("") }
    var username = remember { mutableStateOf("") }
    var email = remember { mutableStateOf("") }

    val keyboardHeight = WindowInsets.ime.getBottom(LocalDensity.current)
    val coroutineScope = rememberCoroutineScope()
    var scrollState = rememberScrollState()

    LaunchedEffect(key1 = keyboardHeight){
        coroutineScope.launch {
            scrollState.scrollBy(keyboardHeight.toFloat())
        }
    }

    Column(Modifier.padding(15.dp)) {
        ActionButton(size = 40, icon = Icons.Default.ArrowBack, onClick = navigateBack)

        Spacer(modifier = Modifier.height(space.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Column {
                Spacer(Modifier.height(5.dp))
                Prompt(text = "What's your name?")

                InputField(text = name, label = "Full Name")

                Spacer(Modifier.height(5.dp))
                Prompt(text = "Choose a username")

                InputField(text = username, label = "Username")

                Spacer(Modifier.height(5.dp))
                Prompt(text = "What's your email?")

                InputField(text = email, label = "Email")
            }
        }
        Column (
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom
        ){
            EnterButton(text = "Next", ){
                if (username.value != ""){
                    viewModel.accountName = username.value
                    navigateToPassword()
                }
            }
        }
    }
}

@Composable
fun Prompt(text: String){
    Text(
        text = text,
        style = TextStyle(fontSize = 25.sp),
        modifier = Modifier.fillMaxWidth()
    )
}


