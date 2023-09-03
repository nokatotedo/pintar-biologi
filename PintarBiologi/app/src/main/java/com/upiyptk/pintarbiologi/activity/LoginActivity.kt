package com.upiyptk.pintarbiologi.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.upiyptk.pintarbiologi.R

class LoginActivity: AppCompatActivity() {
    private lateinit var btnClose: ImageView
    private lateinit var etIdname: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: AppCompatButton
    private lateinit var ref: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btnClose = findViewById(R.id.button_close)
        etIdname = findViewById(R.id.et_idname)
        etPassword = findViewById(R.id.et_password)
        btnLogin = findViewById(R.id.button_login)
        ref = FirebaseDatabase.getInstance().reference

        btnClose.setOnClickListener {
            Intent(this@LoginActivity, SchoolActivity::class.java).also {
                startActivity(it)
            }
        }

        btnLogin.setOnClickListener {
            if(etIdname.text.isEmpty()) {
                etIdname.error = "Mohon Isi"
                return@setOnClickListener
            }
            if(etPassword.text.isEmpty()) {
                etPassword.error = "Mohon Isi"
                return@setOnClickListener
            }

            ref.child("teacher").child("name").get().addOnSuccessListener { it1 ->
                if(it1.value.toString() == etIdname.text.toString()) {
                    ref.child("teacher").child("password").get().addOnSuccessListener { it2 ->
                        if(it2.value.toString() == etPassword.text.toString()) {
                            Intent(this@LoginActivity, MainActivity::class.java).also {
                                it.putExtra(MainActivity.EXTRA_LOGIN, 1)
                                startActivity(it)
                            }
                        } else {
                            Toast.makeText(this@LoginActivity, "Nama/Kata Sandi Salah", Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "Nama/Kata Sandi Salah", Toast.LENGTH_LONG).show()
                }
            } .addOnFailureListener {
                Toast.makeText(this@LoginActivity, "Error", Toast.LENGTH_LONG).show()
            }
        }
    }
}