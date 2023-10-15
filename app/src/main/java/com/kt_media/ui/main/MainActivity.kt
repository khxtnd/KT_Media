package com.kt_media.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kt_media.R
import com.kt_media.databinding.ActivityMainBinding
import com.kt_media.domain.constant.CHILD_USER
import com.kt_media.domain.constant.NAME_INTENT_LOGIN_WITH
import com.kt_media.domain.constant.TITLE_IMAGE
import com.kt_media.domain.constant.TITLE_MUSIC
import com.kt_media.domain.constant.TITLE_VIDEO
import com.kt_media.domain.constant.VAL_INTENT_LOGIN_EMAIL
import com.kt_media.domain.entities.User
import com.kt_media.ui.login.LoginActivity
import com.kt_media.ui.profile.ProfileActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var ivAvatarNav: ImageView
    private lateinit var tvUsernameNav: TextView

    private var loginWith= VAL_INTENT_LOGIN_EMAIL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loginWith= intent.getStringExtra(NAME_INTENT_LOGIN_WITH).toString()
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_ma) as NavHostFragment
        val navController = navHostFragment.navController
        binding.btNavMa.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.musicsFragment ->
                    binding.tvTitleMa.text = TITLE_MUSIC

                R.id.videosFragment ->
                    binding.tvTitleMa.text = TITLE_VIDEO

                R.id.imagesFragment ->
                    binding.tvTitleMa.text = TITLE_IMAGE

            }
        }
        binding.ivToggleMa.setOnClickListener {
            binding.layoutMa.openDrawer(GravityCompat.START)
        }
        firebaseAuth = FirebaseAuth.getInstance()

        binding.navMa.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.mLogout -> {
                    logOut()
                }
                R.id.mAccount ->{
                    updateProfile()
                }

            }
            return@setNavigationItemSelectedListener true
        }
        setNav(loginWith)
    }

    private fun updateProfile() {
        val intent = Intent(this@MainActivity, ProfileActivity::class.java)
        startActivity(intent)
    }

    private fun setNav(loginWith: String) {
        val headerView: View = binding.navMa.getHeaderView(0)
        ivAvatarNav=headerView.findViewById(R.id.cir_iv_avatar_nav)
        tvUsernameNav=headerView.findViewById(R.id.tv_username_nav)

        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val reference = FirebaseDatabase.getInstance().getReference(CHILD_USER).child(firebaseUser!!.uid)
        reference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                tvUsernameNav.text = user!!.name

                if (user.image != "") {
                    Glide.with(this@MainActivity).load(user.image).into(ivAvatarNav)
                }
            }
            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun logOut(){
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder
            .setTitle(R.string.logout)
            .setIcon(R.drawable.ic_logout_40)
            .setMessage(R.string.noty_logout)
            .setPositiveButton(R.string.logout) { _, _ ->
                firebaseAuth.signOut()
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                intent.flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton(R.string.back) { dialog, _ ->
                dialog.cancel()
            }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}
