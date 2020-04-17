package com.example.carpool_recycling_app

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.carpool_recycling_app.data.model.User
import com.example.carpool_recycling_app.ui.login.LoginActivity
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.profile.*

class ProfileActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var userNameTextView: TextView
    private lateinit var numPlasticTextView: TextView
    private lateinit var numGlassTextView: TextView
    private lateinit var numCardboardTextView: TextView
    private lateinit var numAluminumTextView: TextView
    private lateinit var addRecyclablesButton: Button
    private lateinit var editProfile: ImageButton
    private lateinit var auth: FirebaseAuth
    lateinit var toolbar: Toolbar
    lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView

    var username: String = ""
    var numPlastic: Int = 0
    var numGlass: Int = 0
    var numCardboard: Int = 0
    var numAluminum: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        auth = FirebaseAuth.getInstance() //connects to DB
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        //val selectPhotoButton = findViewById<Button>(R.id.selectphoto_button_register)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, 0, 0
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)


    }

    override fun onStart() {
        super.onStart()
        fetchCurrentUser()
        userNameTextView = findViewById(R.id.profile_username_textview)
        numPlasticTextView = findViewById(R.id.profile_numplastic_textview)
        numAluminumTextView = findViewById(R.id.profile_numaluminum_textview)
        numGlassTextView = findViewById(R.id.profile_numglass_textview)
        numCardboardTextView = findViewById(R.id.profile_numcardboard_textview)
        addRecyclablesButton = findViewById(R.id.profile_addrecyclables_button)
        editProfile = findViewById(R.id.profile_settings_imagebutton)

        addRecyclablesButton.setOnClickListener {
            //Log.d("ProfileActivity", "Submit group button clicked")
            val intent = Intent(this, AddRecyclablesActivity::class.java)
            startActivity(intent)
        }

        editProfile.setOnClickListener{
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
        }
    }

    private fun fetchCurrentUser() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
//        Log.d("ProfileActivity", "Current User: $ref")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val currentUser = p0.getValue(User::class.java)

                username = currentUser?.username.toString()
                numAluminum = currentUser!!.numAluminum
                numCardboard = currentUser!!.numPaper
                numGlass = currentUser!!.numGlass
                numPlastic = currentUser!!.numPlastic

                userNameTextView.setText("@" + username)
                numGlassTextView.setText(numGlass.toString())
                numAluminumTextView.setText(numAluminum.toString())
                numCardboardTextView.setText(numCardboard.toString())
                numPlasticTextView.setText(numPlastic.toString())

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

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_message -> {
                Toast.makeText(this, "Profile clicked", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LatestMessagesActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_create_group -> {
                Toast.makeText(this, "Groups clicked", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, CreateGroupActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_profile -> {
                Toast.makeText(this, "Profile clicked", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_profile -> {
                Toast.makeText(this, "Update clicked", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_map -> {
                Toast.makeText(this, "Map clicked", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MapsActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_sign_out -> {
                Toast.makeText(this, "Sign out clicked", Toast.LENGTH_SHORT).show()
                auth.signOut()
                val toast = Toast.makeText(applicationContext, "Signed out", Toast.LENGTH_SHORT)
                toast.show()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}

