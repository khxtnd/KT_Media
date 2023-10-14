package com.kt_media.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.kt_media.R
import com.kt_media.databinding.ActivityLoginBinding
import com.kt_media.domain.constant.NAME_INTENT_LOGIN_WITH
import com.kt_media.domain.constant.VAL_INTENT_LOGIN_EMAIL
import com.kt_media.ui.main.MainActivity
import com.kt_media.ui.register.RegisterActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var firebaseUser: FirebaseUser ?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null) {
            moveToMain(VAL_INTENT_LOGIN_EMAIL)
        }
        binding.btnLoginLa.setOnClickListener {
            loginWithEmail()
        }
        binding.btnRegisterLa.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.ivGoogle.setOnClickListener {

        }

    }

    private fun loginWithEmail() {
        val email = binding.etEmailLa.text.toString()
        val password = binding.etPasswordLa.text.toString()
        if (email.isEmpty() && password.isEmpty()) {
            toastNotification(R.string.noty_invalid.toString())
        } else {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        moveToMain(VAL_INTENT_LOGIN_EMAIL)
                    } else {
                        toastNotification(R.string.noty_account.toString())
                    }
                }
        }
    }

    private fun moveToMain(loginWith: String) {
        var intent = Intent(this@LoginActivity, MainActivity::class.java)
        intent.putExtra(NAME_INTENT_LOGIN_WITH, loginWith)
        startActivity(intent)
        finish()
    }

    private fun toastNotification(notification: String) {
        Toast.makeText(
            applicationContext,
            notification,
            Toast.LENGTH_SHORT
        ).show()
    }
}
