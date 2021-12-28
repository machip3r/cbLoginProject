package com.compubac.proyecto1

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class RegisterActivity : AppCompatActivity() {
    private var txtName : EditText ?= null
    private var rGroupGender : RadioGroup ?= null
    private var txtEmail : EditText ?= null
    private var txtPassword : EditText ?= null
    private var pBarLoading : ProgressBar ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        if (SharedPrefManager.getInstance(this).isLoggedIn) {
            finish()
            startActivity(Intent(this, HomeActivity::class.java))

            return
        }

        val lblLogin = findViewById<TextView>(R.id.lblLoginButton)
        val btnRegister = findViewById<Button>(R.id.btnRegister)

        txtName = findViewById<EditText>(R.id.txtName)
        rGroupGender = findViewById<RadioGroup>(R.id.rGroupGender)
        txtEmail = findViewById<EditText>(R.id.txtEmail)
        txtPassword = findViewById<EditText>(R.id.txtPassword)
        pBarLoading = findViewById<ProgressBar>(R.id.pBarLoading)

        btnRegister.setOnClickListener { register() }

        lblLogin.setOnClickListener { finish() }
    }

    private fun register() {
        val username: String = txtName?.text.toString()
        val gender = (rGroupGender?.getCheckedRadioButtonId()?.let { findViewById<View>(it) } as RadioButton).text.toString()
        val email: String = txtEmail?.text.toString()
        val password: String = txtPassword?.text.toString()

        if (TextUtils.isEmpty(username)) {
            txtName?.error = "Por favor introduce un nombre"
            txtName?.requestFocus()

            return
        }

        if (TextUtils.isEmpty(email)) {
            txtEmail?.error = "Por favor introduce un correo electrónico"
            txtEmail?.requestFocus()

            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            txtEmail?.error = "Ingresa un correo electrónico válido"
            txtEmail?.requestFocus()

            return
        }

        if (TextUtils.isEmpty(password)) {
            txtPassword?.error = "Por favor introduce una contraseña"
            txtPassword?.requestFocus()

            return
        }

        val stringRequest : StringRequest = object : StringRequest(Request.Method.POST, URLs.URL_REGISTER,
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
                params["email"] = email
                params["password"] = password
                params["gender"] = gender

                return params
            }
        }

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest)
    }
}