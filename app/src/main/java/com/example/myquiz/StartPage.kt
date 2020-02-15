package com.example.myquiz

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore

class StartPage : AppCompatActivity() {

    lateinit var mDatabase : DatabaseReference
    val db = FirebaseFirestore.getInstance()
    var user = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_startpage)

        val nameTxt = findViewById<TextView>(R.id.displayText)
        var uid = user!!.uid

        db.collection("users").document(uid).get().addOnSuccessListener { document ->
            nameTxt.text = "Welcome " + document["name"]
        }
        .addOnFailureListener { exception ->
            Log.w("startPage", "Error getting documents.", exception)
        }

        /*mDatabase = FirebaseDatabase.getInstance().getReference("Names")

        mDatabase.child(uid).child("Names").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                val result = snapshot.child("Names").toString()
                nameTxt.text = "Welcome" + result
            }
        })
        */
    }
}