package com.example.carpool_recycling_app

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.carpool_recycling_app.data.model.SimpleGroup
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.IgnoreExtraProperties

class CreateGroupActivity : AppCompatActivity() {

    private lateinit var groupName: EditText
    private lateinit var submitGroupButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_group)
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
}