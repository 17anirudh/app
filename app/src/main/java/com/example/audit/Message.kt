package com.example.audit

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Message : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_message)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val gID = findViewById<FloatingActionButton>(R.id.searchGID)
        val sendBtn = findViewById<FloatingActionButton>(R.id.sendMsg)
        val cancelBtn = findViewById<FloatingActionButton>(R.id.cancel)
        val verifyBtn = findViewById<FloatingActionButton>(R.id.verifyBtn)
        val gUsername = findViewById<EditText>(R.id.gUsername)
        val plateNum = findViewById<EditText>(R.id.plateNum)
        val company = findViewById<EditText>(R.id.company)
        val time = findViewById<EditText>(R.id.time)
        val purpose = findViewById<EditText>(R.id.purpose)
        val getPhone = findViewById<EditText>(R.id.getPhone)
        val firebase = FirebaseFirestore.getInstance()
        val mAuth = FirebaseAuth.getInstance()
        var gNumber = ""
        val user = mAuth.currentUser
        var name = ""
        var dll = ""

        if (user != null) {
            val uid = user.uid
            firebase.collection("users").document(uid).get().addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        dll = document.getString("dll").toString()
                        name = document.getString("name").toString()
                    } else {
                        Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        gID.setOnClickListener {
            if (gUsername.text.toString().isNotEmpty()) {
                firebase.collection("users").whereEqualTo("role", "Gatekeeper").whereEqualTo("username", gUsername.text.toString()).get().addOnSuccessListener {
                    if (!it.isEmpty) {
                        Toast.makeText(this, "Valid gatekeeper", Toast.LENGTH_SHORT).show()
                        gNumber = it.documents[0].getString("phone").toString()
                        return@addOnSuccessListener
                    }
                    else {
                        Toast.makeText(this, "Invalid gatekeeper", Toast.LENGTH_SHORT).show()
                        gUsername.error = "Not found"
                    }
                }
            }
        }
        cancelBtn.setOnClickListener {
            startActivity(Intent(this, FreshT::class.java))
        }

        verifyBtn.setOnClickListener {
            val vehicleNumberPlateRegex = "^[A-Z]{2}[ -]?[0-9]{2}[ -]?[A-Z]{1,2}[ -]?[0-9]{4}$".toRegex()
            if (!vehicleNumberPlateRegex.matches(plateNum.text.toString())) {
                plateNum.error = "Invalid Vehicle Number Plate"
                return@setOnClickListener
            }
            else if (company.text.toString().isEmpty() || time.text.toString().isEmpty() || purpose.text.toString().isEmpty() || plateNum.text.toString().isEmpty() || gUsername.error != null || getPhone.text.toString().isEmpty()) {
                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else {
                Toast.makeText(this, "Verification successful", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
        }

        sendBtn.setOnClickListener {
            val data = hashMapOf(
                "tPhone" to getPhone.text.toString(),
                "gNumber" to gNumber,
                "name" to name,
                "dll" to dll,
                "vehicleNumberPlate" to plateNum.text.toString(),
                "company" to company.text.toString(),
                "time" to time.text.toString(),
                "purpose" to purpose.text.toString()
            )
            firebase.collection("messages").add(data).addOnSuccessListener {
                if (it != null) {
                    Toast.makeText(this, "Message sent", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, FreshT::class.java))
                    finish()
                }
                else {
                    Toast.makeText(this, "Message not sent", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, FreshT::class.java))
                    finish()
                }
            }
        }

    }
}