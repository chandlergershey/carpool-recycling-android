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
import com.example.carpool_recycling_app.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_profile_setup.*
import java.util.*

class EditProfileActivity : AppCompatActivity() {

    private lateinit var selectPhotoButton: Button
    private lateinit var saveProfileButton: Button
    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText

    var selectedPhotoUri: Uri? = null
    var currentPhotoUri: Uri? = null
    var currentFirstName: String = ""
    var currentLastName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
    }

    override fun onStart() {
        super.onStart()
        selectPhotoButton = findViewById(R.id.editprofile_selectphoto_button)
        saveProfileButton = findViewById(R.id.editprofile_saveprofile_button)
        firstNameEditText = findViewById(R.id.editprofile_firstname_edittext)
        lastNameEditText = findViewById(R.id.editprofile_lastname_edittext)

        selectPhotoButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

        saveProfileButton.setOnClickListener {
            uploadImageToFirebaseStorage()
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            // proceed and check what the selected image was
            Log.d("ProfileActivity", "Photo was selected")

            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)


            editprofile_selectphoto_imageview.setImageBitmap(bitmap)

            editprofile_selectphoto_button.alpha = 0f

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
        val refFirstName = FirebaseDatabase.getInstance().getReference("/users/$uid/firstname")
        val refLastName = FirebaseDatabase.getInstance().getReference("/users/$uid/lastname")
        val refProfilePicture = FirebaseDatabase.getInstance().getReference("/users/$uid/profileImageUrl")

        if(lastNameEditText.text.toString() != ""){
            currentLastName = lastNameEditText.text.toString()
            refLastName.setValue(currentLastName).addOnSuccessListener {
                Log.d("EditProfileActivity", "Last name submitted")
            }
        }
        if(firstNameEditText.text.toString() != ""){
            currentFirstName = firstNameEditText.text.toString()
            refFirstName.setValue(currentFirstName).addOnSuccessListener {
                Log.d("EditProfileActivity", "First Name submitted")
            }
        }
        if(profileImageUrl != null){
            refProfilePicture.setValue(profileImageUrl).addOnSuccessListener {
                Log.d("EditProfileActivity", "Selected photo")
            }
        }

        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
    }
}
