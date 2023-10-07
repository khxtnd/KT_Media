package com.kt_media.ui.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.kt_media.R
import com.kt_media.databinding.ActivityRegisterBinding
import com.kt_media.ui.login.LoginActivity
import com.kt_media.ui.main.MainActivity

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
        binding.btnRegisterRa.setOnClickListener {
            setActionRegister()
        }
        binding.tvLoginRa.setOnClickListener {
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
    private fun setActionRegister(){
        val email = binding.etEmailRa.text.toString()
        val password = binding.etPasswordRa.text.toString()
        val configPassword =binding.etConfigPasswordRa.text.toString()

        if (email.isEmpty() || password.isEmpty()){
            Toast.makeText(applicationContext, R.string.notify_invalid, Toast.LENGTH_SHORT).show()
        } else if(password!=configPassword){
            Toast.makeText(applicationContext, R.string.notify_config_pass, Toast.LENGTH_SHORT).show()
        } else {
            registerUser(email, password)
        }
    }
    private fun registerUser(email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    val user: FirebaseUser? = firebaseAuth.currentUser
                    val userId: String = user!!.uid
                    databaseReference =
                        FirebaseDatabase.getInstance().getReference("Users").child(userId)

                    val hashMap: HashMap<String, String> = HashMap()
                    hashMap["userId"] = userId
                    hashMap["userName"] = email
                    hashMap["userImg"] = ""

                    databaseReference.setValue(hashMap).addOnCompleteListener(this) { it ->
                        if (it.isSuccessful) {
                            val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                }
            }

    }
}