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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.example.instagram.ActionButton
import com.example.instagram.EnterButton
import com.example.instagram.PasswordField
import com.example.instagram.navigation.NavigationDestination
import com.example.instagram.profile.ProfileDestination
import kotlinx.coroutines.launch

object PasswordDestination : NavigationDestination {
    override val route = "New Account Password"
    override val titleRes = "Password"
    const val itemIdArg = "profile"
    val routeWithArgs = "${ProfileDestination.route}/{$itemIdArg}"
}



@Composable
fun CreatePasswordScreen(
    navigateToPicture: () -> Unit,
    navigateBack: () -> Unit
){
    var password = remember { mutableStateOf("") }

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
                Prompt(text = "Create a password")

                PasswordField(text = password, label = "Password")


            }
        }
        Column (
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom
        ){
            EnterButton(text = "Next", onClick = navigateToPicture)
        }
    }
}