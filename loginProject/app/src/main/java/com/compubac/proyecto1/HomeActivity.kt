package com.compubac.proyecto1

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {
    private var lblID : TextView?= null
    private var lblName : TextView?= null
    private var lblGender : TextView?= null
    private var lblEmail : TextView?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        if (SharedPrefManager.getInstance(this).isLoggedIn) {
            lblID = findViewById<TextView>(R.id.lblID)
            lblName = findViewById<TextView>(R.id.lblName)
            lblGender = findViewById<TextView>(R.id.lblGender)
            lblEmail = findViewById<TextView>(R.id.lblEmail)

            val btnLogout = findViewById<Button>(R.id.btnLogout)
            val user = SharedPrefManager.getInstance(this).user

            lblID?.text = user.id.toString()
            lblName?.text = user.name.toString()
            lblGender?.text = user.gender.toString()
            lblEmail?.text = user.email.toString()

            btnLogout.setOnClickListener{ SharedPrefManager.getInstance(this).logout() }
        } else {
            finish()
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}