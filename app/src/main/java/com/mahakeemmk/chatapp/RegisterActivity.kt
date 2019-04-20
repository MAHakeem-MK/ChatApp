package com.mahakeemmk.chatapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth:FirebaseAuth

    override fun onStart() {
        super.onStart()
        val currentUser:FirebaseUser? = auth.currentUser
        Log.d("register","already signed In")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

        register_button.setOnClickListener {
            val name = name_edit_text.text.toString()
            val email = email_edit_text.text.toString()
            val password = password_edit_text.text.toString()
            Log.d("register","Name:$name")

            if (email.isEmpty()||password.isEmpty()||name.isEmpty()) {
                Toast.makeText(this,"please fill all the fields",Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener {
                    if(!it.isSuccessful) return@addOnCompleteListener

                    Log.d("register","registration successful")
                    val uid = auth.uid?:""
                    val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
                    val user = User(uid,name)
                    ref.setValue(user)
                        .addOnSuccessListener {
                            Log.d("register","name saved")
                        }
                    val intent = Intent(this,LoginActivity::class.java)
                    startActivity(intent)

                }.addOnFailureListener {
                    Toast.makeText(this,it.message,Toast.LENGTH_LONG).show()
                }

        }

        textButton_signIn.setOnClickListener {
            Log.d("register","you clicked sign in")
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }
    }

}

class User(val uid:String,val name:String) {
    constructor():this("","")
}
