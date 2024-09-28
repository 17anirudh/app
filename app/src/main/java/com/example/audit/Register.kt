package com.example.audit

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Register : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val gButton = findViewById<Button>(R.id.gButton)
        val tButton = findViewById<Button>(R.id.button3)
        val textView = findViewById<TextView>(R.id.textView)

        gButton.setOnClickListener {
            val intent = Intent(this, RegisterG::class.java)
            startActivity(intent)
        }

        tButton.setOnClickListener {
            val intent = Intent(this, RegisterT::class.java)
            startActivity(intent)
        }

        textView.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }
    }
}