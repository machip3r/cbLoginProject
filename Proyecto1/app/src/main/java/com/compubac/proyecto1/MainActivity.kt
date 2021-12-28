package com.compubac.proyecto1

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class MainActivity : AppCompatActivity() {
    private var txtUser : EditText ?= null
    private var txtPassword : EditText ?= null
    private var pBarLoading : ProgressBar ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish()
            startActivity(Intent(this, HomeActivity::class.java))
        }

        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val lblRegister = findViewById<TextView>(R.id.lblRegisterButton)

        txtUser = findViewById<EditText>(R.id.txtUser)
        txtPassword = findViewById<EditText>(R.id.txtPassword)
        pBarLoading = findViewById<ProgressBar>(R.id.pBarLoading)

        btnLogin.setOnClickListener { login() }

        lblRegister.setOnClickListener { startActivity(Intent(this, RegisterActivity::class.java)) }
    }

    private fun login() {
        val username : String = txtUser?.text.toString()
        val password : String = txtPassword?.text.toString()

        if (TextUtils.isEmpty(username)) {
            txtUser?.error = "Por favor introduce tu correo electrónico"
            txtUser?.requestFocus()

            return
        }

        if (TextUtils.isEmpty(password)) {
            txtPassword?.error = "Por favor introduce tu contraseña"
            txtPassword?.requestFocus()

            return
        }

        val stringRequest : StringRequest = object : StringRequest(Request.Method.POST, URLs.URL_LOGIN,
            { response ->
                pBarLoading?.visibility = View.VISIBLE

                try {
                    val obj = JSONObject(response)

                    if (!obj.getBoolean("error")) {
                        Toast.makeText(this, obj.getString("message"), Toast.LENGTH_SHORT).show()

                        val userJson = obj.getJSONObject("user")

                        val user = User(
                            userJson.getInt("id"),
                            userJson.getString("username"),
                            userJson.getString("email"),
                            userJson.getString("gender")
                        )

                        SharedPrefManager.getInstance(this).userLogin(user)

                        pBarLoading?.visibility = View.GONE

                        finish()
                        startActivity(Intent(this, HomeActivity::class.java))
                    } else {
                        pBarLoading?.visibility = View.GONE

                        Toast.makeText(this, obj.getString("message"), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) { e.printStackTrace() }
            }, { error ->
                pBarLoading?.visibility = View.GONE

                Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show()
            }
        )
        {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String>? {
                val params: MutableMap<String, String> = HashMap()
                params["username"] = username
                params["password"] = password

                return params
            }
        }

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest)
    }
}