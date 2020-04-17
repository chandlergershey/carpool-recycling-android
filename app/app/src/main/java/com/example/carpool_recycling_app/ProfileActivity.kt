package com.example.carpool_recycling_app

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.carpool_recycling_app.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.latest_message_row.view.*
import java.util.*

class ProfileActivity : AppCompatActivity() {

    private lateinit var selectPhotoButton: Button
    private lateinit var saveProfileButton: Button
    private lateinit var userNameEditText: EditText
    private lateinit var userNameTextView: TextView

    var username: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        fetchCurrentUser()
    }

    override fun onStart() {
        super.onStart()
        selectPhotoButton = findViewById(R.id.selectphoto_button_register)
        saveProfileButton = findViewById(R.id.saveprofile_button)
        userNameEditText = findViewById(R.id.username_edittext)
        userNameTextView = findViewById(R.id.username_textview)

        val thisUser = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("users/$thisUser")

        selectPhotoButton.setOnClickListener {
            Log.d("ProfileActivity", "Submit group button clicked")

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)

        }

        saveProfileButton.setOnClickListener {
            uploadImageToFirebaseStorage()
        }
    }

    private fun fetchCurrentUser() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        Log.d("ProfileActivity", "Current User: $ref")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val currentUser = p0.getValue(User::class.java)
                username = currentUser?.username.toString()
                userNameTextView.setText(username)
                Log.d("ProfileActivity", "Current User: $username")
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }

    var selectedPhotoUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            // proceed and check what the selected image was
            Log.d("ProfileActivity", "Photo was selected")

            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

//            val bitmapDrawable = BitmapDrawable(bitmap)
//            selectPhotoButton.setBackgroundDrawable(bitmapDrawable)

            selectphoto_imageview_register.setImageBitmap(bitmap)

            selectphoto_button_register.alpha = 0f

        }
    }

    private fun uploadImageToFirebaseStorage() {

        if (selectedPhotoUri == null) return
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!).addOnSuccessListener {
            Log.d("ProfileActivity", "Successfully uploaded image")

            ref.downloadUrl.addOnSuccessListener {
                it.toString()
                Log.d("ProfileActivity", "File Location: $it")

                saveUserToFirebaseDatabase(it.toString())
            }
        }.addOnFailureListener {

        }
    }

    private fun saveUserToFirebaseDatabase(profileImageUrl: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user = User(uid, userNameEditText.text.toString(), profileImageUrl, "", "", 0, 0, 0, 0, "")

        ref.setValue(user).addOnSuccessListener {
            Log.d("ProfileActivity", "We saved the user to Firebase Database")

            val intent = Intent(this, LatestMessagesActivity::class.java)
            //intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }


}

