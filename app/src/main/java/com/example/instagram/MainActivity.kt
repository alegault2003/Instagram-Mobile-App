package com.example.instagram

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.instagram.ui.theme.InstagramTheme
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.storage.ktx.storage

class MainActivity : ComponentActivity() {

    val contactsViewModel = ContactsViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            InstagramTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val db = Firebase.firestore

                    val storageRef = Firebase.storage.reference

                    Instagram(db, contactsViewModel)
                }
            }
        }
    }
}

class ContactsViewModel: ViewModel(){
    var msg: MutableLiveData<Message> = MutableLiveData()
    var msgList: MutableLiveData<MutableList<Message>> = MutableLiveData(mutableListOf<Message>())

    var user: MutableLiveData<Account> = MutableLiveData()
    var userList: MutableLiveData<MutableList<Account>> = MutableLiveData(mutableListOf<Account>())

    var messageUsers: MutableLiveData<MutableList<Account>> = MutableLiveData(mutableListOf<Account>())

    var accountName: String = "agathe.legault"
    lateinit var currentUser: Account
}

