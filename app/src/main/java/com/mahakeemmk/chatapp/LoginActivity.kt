package com.mahakeemmk.chatapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        signIn_button.setOnClickListener {
            val email = email_signIn_edit.text.toString()
            val password = password_signIn_edit.text.toString()

            Log.d("sign in","Email:$email")

            if (email.isEmpty()||password.isEmpty()) {
                Toast.makeText(this,"please fill all the fields", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
                .addOnCompleteListener {
                    if(!it.isSuccessful) return@addOnCompleteListener
                    Log.d("login","successful")
                    val intent = Intent(this,Messages::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }.addOnFailureListener {
                    Log.d("login","failed ${it.message}")
                    Toast.makeText(this,it.message,Toast.LENGTH_LONG).show()
                }
        }
    }
}
