package com.example.myquiz

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.myquiz.R.id.loginButton
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth

class  Login : AppCompatActivity() {
    val mAuth = FirebaseAuth.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val loginButton = findViewById<Button>(loginButton)
        loginButton.setOnClickListener{ v->
            login()
        }
    }

    private fun login(){
        val emailTextField = findViewById<EditText>(R.id.emailTextField)
        val passwordTextField = findViewById<EditText>(R.id.passwordTextField)

        var email = emailTextField.text.toString()
        var password = passwordTextField.text.toString()

         if (email.isNotEmpty() && password.isNotEmpty()) {
             mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, OnCompleteListener { task ->
                 if(task.isSuccessful){
                     startActivity(Intent(this, StartPage :: class.java ))
                     Toast.makeText(this, "Successfully logged in ", Toast.LENGTH_LONG).show()
                 }else{
                     Toast.makeText(this,"Error ",Toast.LENGTH_LONG).show()
                 }
             })
         } else {
             Toast.makeText(this, "Please fill up all the fields :|", Toast.LENGTH_SHORT).show()}
    }
}
