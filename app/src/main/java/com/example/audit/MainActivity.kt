package com.example.audit

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val auth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()

        if(auth.currentUser != null){
            firestore.collection("users").document(auth.currentUser!!.uid).get().addOnSuccessListener {
                if(it.exists()){
                    val role = it.getString("role")
                    if (role == "Gatekeeper") {
                        startActivity(Intent(this, FreshG::class.java))
                        finish()
                    }
                    else if (role == "Transporter") {
                        startActivity(Intent(this, FreshT::class.java))
                        finish()
                    }
                }
            }
        }

        val button = findViewById<Button>(R.id.loginButton)
        button.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }
    }
}