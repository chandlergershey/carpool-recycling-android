package com.example.carpool_recycling_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
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
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_login.*

class GroupActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var groupNameTextView: TextView

    var groupname: String = ""

    private lateinit var auth: FirebaseAuth
    lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group)
    }

    override fun onStart() {
        super.onStart()

        groupNameTextView = findViewById(R.id.group_groupname_textview)

        auth = FirebaseAuth.getInstance() //connects to DB
        drawerLayout = findViewById(R.id.drawer_layout)

        fetchCurrentUser()



    }


    private fun fetchCurrentUser() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
//        Log.d("ProfileActivity", "Current User: $ref")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val currentUser = p0.getValue(User::class.java)

                groupname = currentUser?.groupid.toString()
                groupNameTextView.setText(groupname)

                Log.d("GroupActivity", "Loaded Group: $groupname")
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })
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
