package com.example.carpool_recycling_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.example.carpool_recycling_app.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_registration.*

class AddRecyclablesActivity : AppCompatActivity() {

    // grabs the text view
    private lateinit var numPlasticTextView: TextView
    private lateinit var numGlassTextView: TextView
    private lateinit var numCardboardTextView: TextView
    private lateinit var numAluminumTextView: TextView
    private lateinit var submitMetrics: Button

    // holds the current number of recyclables
    var numPlastic: Int = 0
    var numGlass: Int = 0
    var numCardboard: Int = 0
    var numAluminum: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_recyclables)
        fetchCurrentUser() // grabs the user state
    }

    override fun onStart() {
        super.onStart()

        // grabs the edit text fields
        numPlasticTextView = findViewById(R.id.usermetrics_numplastic_edittext)
        numAluminumTextView = findViewById(R.id.usermetrics_numaluminum_edittext)
        numGlassTextView = findViewById(R.id.usermetrics_numglass_edittext)
        numCardboardTextView = findViewById(R.id.usermetrics_numcardboard_edittext)
        submitMetrics = findViewById(R.id.usermetrics_submitmetrics_button)

        submitMetrics.setOnClickListener {
            addUserMetricsToFirebaseDatabase()
        }

    }

    private fun fetchCurrentUser() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val currentUser = p0.getValue(User::class.java)

                numAluminum = currentUser!!.numAluminum
                numCardboard = currentUser!!.numPaper
                numGlass = currentUser!!.numGlass
                numPlastic = currentUser!!.numPlastic

                Log.d("ProfileActivity", "Current User: $username")
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }

    private fun addUserMetricsToFirebaseDatabase() {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val refPlastic = FirebaseDatabase.getInstance().getReference("/users/$uid/numPlastic")
        val refGlass = FirebaseDatabase.getInstance().getReference("/users/$uid/numGlass")
        val refCardboard = FirebaseDatabase.getInstance().getReference("/users/$uid/numPaper")
        val refAluminum = FirebaseDatabase.getInstance().getReference("/users/$uid/numAluminum")


        var enteredPlastic = 0
        var enteredGlass = 0
        var enteredCardboard = 0
        var enteredAluminum = 0
        if(numPlasticTextView.text.toString() != ""){
            enteredPlastic = Integer.parseInt(numPlasticTextView.text.toString())
        }
        if(numGlassTextView.text.toString() != ""){
            enteredGlass = Integer.parseInt(numGlassTextView.text.toString())
        }
        if(numCardboardTextView.text.toString() != ""){
            enteredCardboard = Integer.parseInt(numCardboardTextView.text.toString())
        }
        if(numAluminumTextView.text.toString() != ""){
            enteredAluminum = Integer.parseInt(numAluminumTextView.text.toString())
        }

        val totalPlastic = numPlastic + enteredPlastic
        val totalGlass = numGlass + enteredGlass
        val totalCardboard = numCardboard + enteredCardboard
        val totalAluminum = numAluminum + enteredAluminum

        refPlastic.setValue(totalPlastic).addOnSuccessListener {
            Log.d("AddRecyclablesActivity", "Saved plastic total")
        }

        refGlass.setValue(totalGlass).addOnSuccessListener {
            Log.d("AddRecyclablesActivity", "Saved glass total")
        }

        refCardboard.setValue(totalCardboard).addOnSuccessListener {
            Log.d("AddRecyclablesActivity", "Saved cardboard total")
        }

        refAluminum.setValue(totalAluminum).addOnSuccessListener {
            Log.d("AddRecyclablesActivity", "Saved aluminum total")
        }

        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)

    }
}
