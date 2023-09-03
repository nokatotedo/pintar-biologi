package com.upiyptk.pintarbiologi.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.upiyptk.pintarbiologi.R

class SchoolActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_LOGIN = "extra_login"
    }
    private lateinit var btnClose: ImageView
    private lateinit var btnLogin: ImageView
    private lateinit var bnavSchool: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_school)

        btnClose = findViewById(R.id.button_close)
        bnavSchool = findViewById(R.id.bnav_school)
        btnLogin = findViewById(R.id.button_login)

        val login = intent.getIntExtra(EXTRA_LOGIN, 0)
        if(login == 1) {
            btnLogin.visibility = View.INVISIBLE
        } else {
            btnLogin.visibility = View.VISIBLE
        }

        btnClose.setOnClickListener {
            Intent(this@SchoolActivity, MainActivity::class.java).also {
                it.putExtra(MainActivity.EXTRA_LOGIN, login)
                startActivity(it)
            }
        }

        btnLogin.setOnClickListener {
            Intent(this@SchoolActivity, LoginActivity::class.java).also {
                startActivity(it)
            }
        }

        val navSchool = supportFragmentManager.findFragmentById(R.id.fragmentContainerView)?.findNavController()
        navSchool?.let { bnavSchool.setupWithNavController(it) }
    }
}