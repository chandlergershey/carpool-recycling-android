package com.example.carpool_recycling_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.example.carpool_recycling_app.data.model.User
import com.example.carpool_recycling_app.ui.login.RegistrationActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_latest_messages.*

class LatestMessagesActivity : AppCompatActivity() {

    companion object {
        var currentUser: User? = null
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_messages)

        setUpDummyRows()

        fetchCurrentUser()
        // this checks to see if a user is logged in
        verifyUserIsLoggedIn()
    }

    class LatestMessageRow: Item<GroupieViewHolder>() {
        override fun getLayout(): Int {
            return R.layout.latest_message_row
        }

        override fun bind(viewHolder: GroupieViewHolder, position: Int) {

        }
    }

    private fun setUpDummyRows(){
        val adapter = GroupAdapter<GroupieViewHolder>()

        adapter.add(LatestMessageRow())
        adapter.add(LatestMessageRow())
        adapter.add(LatestMessageRow())
        adapter.add(LatestMessageRow())

        recyclerview_latest_messages.adapter = adapter
    }

    private fun fetchCurrentUser(){
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                currentUser = p0.getValue(User::class.java)
                Log.d("LatestMessages", "Current User: $currentUser?.username")
            }

            override fun onCancelled(p0: DatabaseError) {

            }


        })


    }

    private fun verifyUserIsLoggedIn() {
        val uid = FirebaseAuth.getInstance().uid
        if (uid == null) {
            val intent = Intent(this, RegistrationActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId){
            R.id.menu_new_message -> {
                val intent = Intent(this, NewMessageActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.message_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
}
