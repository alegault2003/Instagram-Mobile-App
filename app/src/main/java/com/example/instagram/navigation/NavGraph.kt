package com.example.instagram.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.instagram.Feed.FeedDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.instagram.ContactsViewModel
import com.example.instagram.Feed.FeedScreen
import com.example.instagram.login.AccountScreen
import com.example.instagram.login.CreateNameScreen
import com.example.instagram.login.CreatePasswordScreen
import com.example.instagram.login.CreatePictureScreen
import com.example.instagram.login.LoginDestination
import com.example.instagram.login.NameDestination
import com.example.instagram.login.PasswordDestination
import com.example.instagram.login.ProfilePictureDestination
import com.example.instagram.messages.ChatDestination
import com.example.instagram.messages.ChatScreen
import com.example.instagram.messages.MessageList
import com.example.instagram.messages.MessageListDestination
import com.example.instagram.messages.NewMessage
import com.example.instagram.messages.NewMessageDestination
import com.example.instagram.profile.PersonalProfileDestination
import com.example.instagram.profile.PersonalProfileScreen
import com.example.instagram.profile.ProfileDestination
import com.example.instagram.profile.ProfileScreen
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun InstaNavHost(
    navController: NavHostController,
    db: FirebaseFirestore,
    contactsViewModel: ContactsViewModel,
    modifier: Modifier = Modifier
){
    NavHost(navController = navController, startDestination = FeedDestination.route, modifier = modifier){
        composable(route = LoginDestination.route){
            AccountScreen(
                navigateToCreate = {navController.navigate(NameDestination.route)},
                navigateToFeed = { navController.navigate(FeedDestination.route) },
                contactsViewModel
            )
        }
        composable(route = NameDestination.route){
            CreateNameScreen(
                navigateToPassword = {navController.navigate(PasswordDestination.route)},
                navigateBack = {navController.navigate(LoginDestination.route)},
                contactsViewModel
            )
        }

        composable(route = PasswordDestination.route){
            CreatePasswordScreen(
                navigateToPicture = { navController.navigate(ProfilePictureDestination.route) },
                navigateBack = {navController.navigate(NameDestination.route)}
            )
        }

        composable(route = ProfilePictureDestination.route){
            CreatePictureScreen(
                navigateToFeed = { navController.navigate(FeedDestination.route) },
                navigateBack = { navController.navigate(PasswordDestination.route) }
            )
        }

        composable(route = FeedDestination.route){
            FeedScreen(
                navigateToPersonalProfile = { navController.navigate(PersonalProfileDestination.route) },
                navigateToProfile = { name -> navController.navigate("${ProfileDestination.route}/$name") },
                navigateToMessages = { navController.navigate(MessageListDestination.route) },
                db = db, viewModel = contactsViewModel
            )
        }

        composable(route = PersonalProfileDestination.route){
            PersonalProfileScreen(
                navigateToPersonalProfile = { navController.navigate(PersonalProfileDestination.route) },
                navigateToFeed = { navController.navigate(FeedDestination.route) },
                contactsViewModel
            )
        }

        composable(
            route = ProfileDestination.routeWithArgs,
            arguments = listOf(navArgument(ProfileDestination.itemIdArg) {
                type = NavType.StringType
            })
        ){
            ProfileScreen(
                navigateToPersonalProfile = { navController.navigate(PersonalProfileDestination.route) },
                navigateToFeed = { navController.navigate(FeedDestination.route) },
                navigateToChat = { name -> navController.navigate("${ChatDestination.route}/$name") },
                db = db, contactsViewModel = contactsViewModel
            )
        }

        composable(route = MessageListDestination.route){
            MessageList(
                navigateBack = { navController.navigate(FeedDestination.route) },
                navigateToNew = { navController.navigate(NewMessageDestination.route) },
                navigateToChat = { name -> navController.navigate("${ChatDestination.route}/$name") },
                viewModel = contactsViewModel,
                db = db
            )
        }

        composable(route = NewMessageDestination.route){
            NewMessage(
                navigateBack = { navController.navigate(MessageListDestination.route) },
                navigateToChat = { name -> navController.navigate("${ChatDestination.route}/$name") },
                viewModel = contactsViewModel,
                db = db
            )
        }

        composable(
            route = ChatDestination.routeWithArgs,
            arguments = listOf(navArgument(ChatDestination.itemIdArg) {
                type = NavType.StringType
            })
        ) {
            ChatScreen(
                navigateBack = { navController.navigate(MessageListDestination.route) },
                viewModel = contactsViewModel,
                db = db
            )
        }
    }
}