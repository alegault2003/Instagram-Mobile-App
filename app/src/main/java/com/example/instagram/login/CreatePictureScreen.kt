package com.example.instagram.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.instagram.ActionButton
import com.example.instagram.EnterButton
import com.example.instagram.navigation.NavigationDestination

object ProfilePictureDestination : NavigationDestination {
    override val route = "New Account Picture"
    override val titleRes = "Profile Picture"
}

@Composable
fun CreatePictureScreen(
    navigateToFeed: () -> Unit,
    navigateBack: () -> Unit
){
    Column(Modifier.padding(15.dp)) {
        ActionButton(size = 40, icon = Icons.Default.ArrowBack, onClick = navigateBack)

        Spacer(modifier = Modifier.height(space.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Column {
                Spacer(Modifier.height(5.dp))
                Prompt(text = "Add a profile picture")

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Profile Picture",
                        tint = Color.LightGray,
                        modifier = Modifier
                            .size(250.dp)
                    )
                }
            }
        }
        Column (
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom
        ){
            // TODO: make "add picture" button change to next when clicked
            EnterButton(text = "Add Picture") { /* TODO */ }
            EnterButton(text = "Skip", onClick = navigateToFeed)
        }
    }
}