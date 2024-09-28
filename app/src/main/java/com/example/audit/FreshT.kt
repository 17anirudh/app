package com.example.audit

import android.content.Intent
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FreshT : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_fresh_t)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val mAuth = FirebaseAuth.getInstance()
        val profileBtn = findViewById<ImageButton>(R.id.logoutG)
        val addBtn = findViewById<FloatingActionButton>(R.id.reload)
        val reload2 = findViewById<FloatingActionButton>(R.id.reload2)
        val linearT = findViewById<LinearLayout>(R.id.linearT)
        val firestore = FirebaseFirestore.getInstance()
        val user = mAuth.currentUser
        var counter = 0
        var dll = ""
        val dataList = mutableListOf<Map<String, String>>()

        if (user != null) {
            val uid = user.uid
            firestore.collection("users").document(uid).get().addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    dll = document.getString("phone").toString()
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

        reload2.setOnClickListener  {
            firestore.collection("messages").whereEqualTo("dll", dll).get().addOnSuccessListener {
                if (!it.isEmpty) {
                    for (document in it.documents) {
                        val data = mutableMapOf<String, String>()
                        data["name"] = document.getString("name").toString()
                        data["vehicleNumberPlate"] = document.getString("vehicleNumberPlate").toString()
                        data["company"] = document.getString("company").toString()
                        data["time"] = document.getString("time").toString()
                        data["purpose"] = document.getString("purpose").toString()
                        data["tPhone"] = document.getString("tPhone").toString()
                        dataList.add(data)
                        counter += 1
                        Toast.makeText(this, "Total Messages: $counter", Toast.LENGTH_SHORT).show()
                    }
                    linearT.removeAllViews()
                    for ((index, data) in dataList.withIndex()) {
                        val cardView = LayoutInflater.from(this).inflate(R.layout.activity_fresh_t, null, false) as CardView
                        val textView = cardView.findViewById<TextView>(R.id.textView)

                        val sb = StringBuilder()
                        sb.append(index + 1)
                        sb.append(". ")
                        sb.append(data["name"]).append(" - ")
                        sb.append(data["vehicleNumberPlate"]).append(" - ")
                        sb.append(data["company"]).append(" - ")
                        sb.append(data["time"]).append(" - ")
                        sb.append(data["purpose"])

                        textView.text = sb.toString()

                        val params = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply {
                            setMargins(16, 16, 16, 16)
                        }
                        cardView.layoutParams = params

                        linearT.addView(cardView)
                    }
                    Toast.makeText(this, "Total Messages: $counter", Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(this, "No messages found", Toast.LENGTH_SHORT).show()
                }
            }

        }


        addBtn.setOnClickListener {
            startActivity(Intent(this, Message::class.java))
        }
    }
}