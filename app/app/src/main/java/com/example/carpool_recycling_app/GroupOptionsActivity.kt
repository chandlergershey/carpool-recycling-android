package com.example.carpool_recycling_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class GroupOptionsActivity : AppCompatActivity() {

    private lateinit var createGroupButton: Button
    private lateinit var joinGroupButton: Button
    private lateinit var groupNameEditText: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_options)
    }

    override fun onStart() {
        super.onStart()

        createGroupButton = findViewById(R.id.groupoptions_creategroup_button)
        joinGroupButton = findViewById(R.id.groupoptions_joingroup_button)
        groupNameEditText = findViewById(R.id.groupoptions_groupname_edittext)

        // page to enter the group parameters
        createGroupButton.setOnClickListener {
            val intent = Intent(this, CreateGroupActivity::class.java)
            startActivity(intent)
        }

        // goal is to query the database for groups. If it exists, then join the group.
        joinGroupButton.setOnClickListener {
            val intent = Intent(this, ProfileSetupActivity::class.java)
            startActivity(intent)
        }


    }
}
