package com.example.audit

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val registerButton = findViewById<TextView>(R.id.registerRedirect)
        registerButton.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
            finish()
        }

        val button = findViewById<Button>(R.id.button)
        val mail = findViewById<EditText>(R.id.loginEmail)
        val password = findViewById<EditText>(R.id.loginPassword)
        val mAuth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()
        var role = ""

        button.setOnClickListener {
            val mailT = mail.text.toString()
            val passwordT = password.text.toString()

            if (mailT == "" || passwordT == "") {
                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            mAuth.signInWithEmailAndPassword(mailT, passwordT).addOnCompleteListener{
                if(it.isSuccessful){
                    Toast.makeText(this, "Logged In Successfully", Toast.LENGTH_LONG).show()
                    val user = mAuth.currentUser
                    val uid = user!!.uid
                    firestore.collection("users").document(uid).get().addOnSuccessListener { document ->
                        if (document != null && document.exists()) {
                            role = document.getString("role").toString()
                            Toast.makeText(this, "Logged In $role", Toast.LENGTH_SHORT).show()
                            if (role == "Gatekeeper") {
                                startActivity(Intent(this, FreshG::class.java))
                                finish()
                            }
                            else {
                                startActivity(Intent(this, FreshT::class.java))
                                finish()
                            }
                        }
                    }
                }
                else{
                    Toast.makeText(this, "Error Occurred" + it.exception.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}