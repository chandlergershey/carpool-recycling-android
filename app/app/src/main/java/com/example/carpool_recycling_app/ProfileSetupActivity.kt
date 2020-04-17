package com.example.carpool_recycling_app

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.example.carpool_recycling_app.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_profile_setup.*
import java.util.*

class ProfileSetupActivity : AppCompatActivity() {

    private lateinit var selectPhotoButton: Button
    private lateinit var saveProfileButton: Button
    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var userNameEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_setup)
    }

    override fun onStart() {
        super.onStart()
        selectPhotoButton = findViewById(R.id.setup_selectphoto_button)
        saveProfileButton = findViewById(R.id.setup_saveprofile_button)
        firstNameEditText = findViewById(R.id.setup_firstname_edittext)
        lastNameEditText = findViewById(R.id.setup_lastname_edittext)
        userNameEditText = findViewById(R.id.setup_username_edittext)

        val thisUser = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("users/$thisUser")

        selectPhotoButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

        saveProfileButton.setOnClickListener {
            uploadImageToFirebaseStorage()
        }
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

            setup_selectphoto_imageview.setImageBitmap(bitmap)

            setup_selectphoto_button.alpha = 0f

        }
    }

    private fun uploadImageToFirebaseStorage() {

        if (selectedPhotoUri == null) return
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!).addOnSuccessListener {
            Log.d("ProfileSetupActivity", "Successfully uploaded image")

            ref.downloadUrl.addOnSuccessListener {
                it.toString()
                Log.d("ProfileSetupActivity", "File Location: $it")

                saveUserToFirebaseDatabase(it.toString())
            }
        }.addOnFailureListener {
            Log.d("ProfileSetupActivity", "File upload failure")
        }
    }

    private fun saveUserToFirebaseDatabase(profileImageUrl: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user = User(uid, userNameEditText.text.toString(), profileImageUrl, firstNameEditText.text.toString(), lastNameEditText.text.toString(), 0, 0, 0, 0, "0")

        ref.setValue(user).addOnSuccessListener {
            Log.d("ProfileSetupActivity", "We saved the user to Firebase Database")

            val intent = Intent(this, LatestMessagesActivity::class.java)
            //intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }
}
