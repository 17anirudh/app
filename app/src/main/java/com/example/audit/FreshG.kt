package com.example.audit

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.setMargins
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FreshG : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_fresh_g)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val mAuth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()
        val user = mAuth.currentUser
        var phone = ""
        var counter = 0
        val dataList = mutableListOf<Map<String, String>>()
        val profileBtn = findViewById<ImageButton>(R.id.logoutG)
        val addBtn = findViewById<FloatingActionButton>(R.id.reload)
        val linearG = findViewById<LinearLayout>(R.id.linearG)

        if (user != null) {
            val uid = user.uid
            firestore.collection("users").document(uid).get().addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    phone = document.getString("phone").toString()
                    Toast.makeText(this, "Logged In $phone", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show()
                }
            }
        }

        profileBtn.setOnClickListener {
            Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show()
            mAuth.signOut()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }


        addBtn.setOnClickListener {
            firestore.collection("messages").whereEqualTo("gNumber", phone).get().addOnSuccessListener {
                if (!it.isEmpty) {
                    for (document in it.documents) {
                        val data = mutableMapOf<String, String>()
                        data["name"] = document.getString("name").toString()
                        data["vehicleNumberPlate"] = document.getString("vehicleNumberPlate").toString()
                        data["company"] = document.getString("company").toString()
                        data["time"] = document.getString("time").toString()
                        data["purpose"] = document.getString("purpose").toString()
                        data["tPhone"] = document.getString("tPhone").toString()
                        counter += 1
                        dataList.add(data)
                        Toast.makeText(this, "Total Messages: $counter", Toast.LENGTH_SHORT).show()
                    }
                    for ((index, data) in dataList.withIndex()) {
                        val cardView = CardView(this).apply {
                            radius = 12f
                            cardElevation = 8f
                            setBackgroundColor(Color.WHITE) // White background for the card
                            layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            ).apply {
                                setMargins(16, 16, 16, 16)
                            }
                        }

                        val textView = TextView(this).apply {
                            setTextColor(Color.BLACK) // Set the text color to black or any visible color
                            textSize = 16f // Optional: Set text size if needed
                            setPadding(16, 16, 16, 16) // Optional: Add padding to the TextView
                        }

                        val sb = StringBuilder().apply {
                            append(index + 1)
                            append(". ")
                            append(data["name"]).append(" - ")
                            append(data["vehicleNumberPlate"]).append(" - ")
                            append(data["company"]).append(" - ")
                            append(data["time"]).append(" - ")
                            append(data["purpose"])
                        }
                        textView.text = sb.toString()

                        // Add the TextView to the CardView
                        cardView.addView(textView)

                        // Add the CardView to the LinearLayout
                        linearG.addView(cardView)

                        Toast.makeText(this, "Added UI", Toast.LENGTH_SHORT).show()
                    }
                    Toast.makeText(this, "Total Messages: $counter", Toast.LENGTH_SHORT).show()
                }
                else {
                    Toast.makeText(this, "No messages found", Toast.LENGTH_SHORT).show()
                }

            }

        }
    }
}