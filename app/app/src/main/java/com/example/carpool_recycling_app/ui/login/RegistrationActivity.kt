package com.example.carpool_recycling_app.ui.login

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.carpool_recycling_app.R
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth


class RegistrationActivity : AppCompatActivity() {
    val TAG = "debug"
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        val button = findViewById<Button>(R.id.submit)
        val emailView = findViewById<EditText>(R.id.email)
        val passwordView = findViewById<EditText>(R.id.password)
        button?.setOnClickListener{
            createUser(emailView.text.toString(), passwordView.text.toString())
        }
    }

    private fun createUser(email: String, password: String) {
        auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task: Task<AuthResult> ->
            if (task.isSuccessful) {
                //Registration OK
                val firebaseUser = auth.currentUser!!
                Log.d(TAG, firebaseUser?.toString())
            } else {
                Log.d(TAG, "Sign up failed")
            }
        }
    }
}
