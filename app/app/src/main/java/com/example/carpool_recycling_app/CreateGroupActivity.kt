package com.example.carpool_recycling_app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.carpool_recycling_app.data.model.SimpleGroup
import com.example.carpool_recycling_app.ui.login.LoginActivity
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.IgnoreExtraProperties

class CreateGroupActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var auth: FirebaseAuth
    private lateinit var groupName: EditText
    private lateinit var submitGroupButton: Button
    lateinit var toolbar: Toolbar
    lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_group)
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
        submitGroupButton = findViewById<Button>(R.id.submit_group_button)
        groupName = findViewById<EditText>(R.id.group_name_edit_text)
        submitGroupButton.setOnClickListener {
            Log.d("CreateGroup", "Submit group button clicked")


            saveNewGroup()

        }

    }

    private fun saveNewGroup() {
        val name = groupName.text.toString().trim()

        // checks to see if the name of the simple group is empty
        if(name.isEmpty()) {
            groupName.error = "Please enter a group name"
            return
        }

        val ref = FirebaseDatabase.getInstance().getReference("groups")

        val groupId = ref.push().key

        val group = SimpleGroup(groupId, name)

        if(groupId != null){
            ref.child(groupId).setValue(group).addOnCompleteListener{
                Toast.makeText(applicationContext, "Hero saved successfully", Toast.LENGTH_LONG).show()
            }
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