package com.example.carpool_recycling_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.example.carpool_recycling_app.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth


val TAG = "debug"
class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = FirebaseAuth.getInstance() //connects to DB
    }
    override fun onStart() {
        super.onStart()
        val signOut = findViewById<Button>(R.id.signOut)
        val createGroup = findViewById<Button>(R.id.create_group_button)
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Log.d(TAG, "Current user is not signed in")
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        else{
            Log.d(TAG, "User is logged in")
            signOut.setOnClickListener{
                auth.signOut()
                val toast = Toast.makeText(applicationContext, "Signed out", Toast.LENGTH_SHORT)
                toast.show()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
            createGroup.setOnClickListener{
                // here is where we are going to jump to the create group view
            }
        }
    }

}
