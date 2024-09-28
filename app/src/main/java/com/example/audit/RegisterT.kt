package com.example.audit

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterT : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register_t)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val username = findViewById<EditText>(R.id.username)
        val button = findViewById<Button>(R.id.regButton)
        val name = findViewById<EditText>(R.id.regName)
        val occupation = findViewById<EditText>(R.id.regOccupation)
        val dob = findViewById<EditText>(R.id.regDob)
        val dll = findViewById<EditText>(R.id.regDll)
        val mail = findViewById<EditText>(R.id.regMail)
        val password = findViewById<EditText>(R.id.regPass)
        val search = findViewById<Button>(R.id.search)
        val mAuth = FirebaseAuth.getInstance()
        val firebase = FirebaseFirestore.getInstance()


        search.setOnClickListener{
            if (username.text.toString().isNotEmpty()) {
                firebase.collection("users").whereEqualTo("username", username.text.toString()).get().addOnSuccessListener {
                    if (!it.isEmpty) {
                        Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show()
                        username.error = "Username already exists"
                        return@addOnSuccessListener
                    }
                    else {
                        username.error = null
                        Toast.makeText(this, "Username available", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

       if (username.error == null) {
           button.setOnClickListener {
               val name = name.text.toString()
               val occupation = occupation.text.toString()
               val dob = dob.text.toString()
               val dll = dll.text.toString()
               val mail = mail.text.toString()
               val password = password.text.toString()
               val regex =
                   "^(([A-Z]{2}[0-9]{2})( )|([A-Z]{2}-[0-9]{2}))((19|20)[0-9]{2})[0-9]{7}$".toRegex() //dll regex

               if (!regex.matches(dll)) {
                   Toast.makeText(this, "Invalid Driving License Number", Toast.LENGTH_SHORT).show()
                   return@setOnClickListener
               }
               if (password.length < 8) {
                   Toast.makeText(
                       this,
                       "Password must be at least 8 characters long",
                       Toast.LENGTH_SHORT
                   ).show()
                   return@setOnClickListener
               }
               if (name.isEmpty() || occupation.isEmpty() || dob.isEmpty() || dll.isEmpty() || mail.isEmpty() || password.isEmpty()) {
                   Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
                   return@setOnClickListener
               }

               firebase.collection("users").whereEqualTo("mail", mail).whereEqualTo("password", password).get().addOnSuccessListener {
                   if(!it.isEmpty){
                       Toast.makeText(this, "User already exists", Toast.LENGTH_SHORT).show()
                       val intent = Intent(this, Login::class.java)
                       startActivity(intent)
                       return@addOnSuccessListener
                   }
               }

               mAuth.createUserWithEmailAndPassword(mail, password).addOnCompleteListener {
                   if (it.isSuccessful)
                   {
                       Toast.makeText(this, "Registered Successfully", Toast.LENGTH_SHORT).show()
                       val role = "Transporter"
                       val user = hashMapOf(
                           "uid" to mAuth.currentUser!!.uid,
                           "username" to username.text.toString(),
                           "password" to password,
                           "name" to name,
                           "occupation" to occupation,
                           "dob" to dob,
                           "dll" to dll,
                           "mail" to mail,
                           "role" to role
                       )
                       firebase.collection("users").document(mAuth.currentUser!!.uid).set(user).addOnSuccessListener{
                           Toast.makeText(this, "Data Added Successfully", Toast.LENGTH_SHORT).show()
                       }
                       startActivity(Intent(this, FreshT::class.java))
                       finish()
                   }
                   else {
                       Toast.makeText(this, "Error Occurred", Toast.LENGTH_LONG).show()
                   }
               }
           }
       }
        else {
            Toast.makeText(this, "Username conflict", Toast.LENGTH_SHORT).show()
       }
    }
}