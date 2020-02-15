package com.example.myquiz

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

class SignUp : AppCompatActivity() {

    val mAuth = FirebaseAuth.getInstance()
    lateinit var mDatabase : DatabaseReference
    val db = FirebaseFirestore.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        mDatabase = FirebaseDatabase.getInstance().getReference("Names")
        val signUpButton = findViewById<Button>(R.id.signUp)
        signUpButton.setOnClickListener{ v->
            register()
        }
    }


    private fun register() {
        val emailText = findViewById<EditText>(R.id.emailText)
        val userNameText = findViewById<EditText>(R.id.userNameText)
        val passWordText = findViewById<EditText>(R.id.passWordText)

        var signEmail = emailText.text.toString()
        var userName = userNameText.text.toString()
        var signPassWord = passWordText.text.toString()

        if(signEmail.isNotEmpty() && userName.isNotEmpty() && signPassWord.isNotEmpty()){
            mAuth.createUserWithEmailAndPassword(signEmail, signPassWord).addOnCompleteListener(this, OnCompleteListener { task ->
                if(task.isSuccessful){
                    val user = mAuth.currentUser
                    val uid = user!!.uid

                    val userValue = hashMapOf(
                        "name" to userName
                    )

                    //mDatabase.child(uid).child("Names").setValue(userName)
                    db.collection("users").document(uid).set(userValue).addOnSuccessListener {
                        startActivity(Intent(this, StartPage :: class.java ))
                        Toast.makeText(this, "Successfully registered :)", Toast.LENGTH_LONG).show()
                    }.addOnFailureListener { e ->
                        Log.w("createUser", "Error adding document", e)
                        Toast.makeText(this, "Error registering, check the connection  :(", Toast.LENGTH_LONG).show()
                    }

                }else{
                    Toast.makeText(this, "Error registering, check the connection  :(", Toast.LENGTH_LONG).show()
                }

            })

        }else{
            Toast.makeText(this,"Please fill up the fields :|", Toast.LENGTH_LONG).show()


        }

    }
}