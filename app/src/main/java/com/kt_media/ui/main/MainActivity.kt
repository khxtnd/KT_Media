package com.kt_media.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import com.kt_media.R
import com.kt_media.databinding.ActivityMainBinding
import com.kt_media.domain.constant.CHILD_DAY_OF_USE
import com.kt_media.domain.constant.CHILD_SONG_FAV
import com.kt_media.domain.constant.CHILD_USED_MINUTE
import com.kt_media.domain.constant.KEY_DAY_OF_USE_ID
import com.kt_media.domain.constant.NAME_INTENT_CHECK_CATEGORY
import com.kt_media.domain.constant.NAME_INTENT_CHECK_VIDEO
import com.kt_media.domain.constant.NAME_INTENT_LOGIN_WITH
import com.kt_media.domain.constant.TITLE_IMAGE
import com.kt_media.domain.constant.TITLE_MUSIC
import com.kt_media.domain.constant.TITLE_SHARED_PREFERENCES
import com.kt_media.domain.constant.TITLE_VIDEO
import com.kt_media.domain.constant.VAL_INTENT_LOGIN_EMAIL
import com.kt_media.domain.constant.VAL_INTENT_VIDEO_FAV
import com.kt_media.domain.entities.DayOfUse
import com.kt_media.ui.login.LoginActivity
import com.kt_media.ui.musics.play_music.PlaySongActivity
import com.kt_media.ui.playlist.PlayListActivity
import com.kt_media.ui.profile.ProfileActivity
import com.kt_media.ui.statistical.StatisticalActivity
import com.kt_media.ui.videos.play_video.PlayVideoActivity
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var dbRefDayOfUse: DatabaseReference
    private lateinit var ivAvatarNav: ImageView
    private lateinit var tvUsernameNav: TextView
    private var dayOfUseId = ""
    private var time = ""
    private var userId = ""

    private var loginWith = VAL_INTENT_LOGIN_EMAIL

    private val userViewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loginWith = intent.getStringExtra(NAME_INTENT_LOGIN_WITH).toString()
        dbRefDayOfUse = FirebaseDatabase.getInstance().getReference(CHILD_DAY_OF_USE)
        userId = FirebaseAuth.getInstance().currentUser?.uid.toString()
        firebaseAuth = FirebaseAuth.getInstance()
        startUsingTime()

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

        binding.navMa.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.mAccount -> {
                    updateProfile()
                }

                R.id.mVideoFav -> {
                    playVideoFav()
                }

                R.id.mSongFav -> {
                    playSongFav()
                }

                R.id.mPlayList -> {
                    navPlayList()
                }

                R.id.mLogout -> {
                    logOut()
                }

                R.id.mStatistical -> {
                    navStatistical()
                }
            }
            return@setNavigationItemSelectedListener true
        }
        setNav(loginWith)

    }

    private fun startUsingTime() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val formattedDate= String.format("%02d-%02d-%04d", day, month, year)

        time=getTime()
        dayOfUseId = "$userId $formattedDate"

        val query= dbRefDayOfUse.child(dayOfUseId)
        query.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(!dataSnapshot.exists()){
                    val dayOfUse = DayOfUse(dayOfUseId, userId, formattedDate, 0, 0)
                    query.setValue(dayOfUse)
                }
            }

            override fun onCancelled(error: DatabaseError) {}

        })
        val sharedPreferences = getSharedPreferences(TITLE_SHARED_PREFERENCES, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(KEY_DAY_OF_USE_ID, dayOfUseId)
        editor.apply()
    }

    private fun navStatistical() {
        val intent = Intent(this@MainActivity, StatisticalActivity::class.java)
        startActivity(intent)
    }

    private fun navPlayList() {
        val intent = Intent(this@MainActivity, PlayListActivity::class.java)
        startActivity(intent)
    }

    private fun playSongFav() {
        val intent = Intent(this@MainActivity, PlaySongActivity::class.java)
        intent.putExtra(NAME_INTENT_CHECK_CATEGORY, CHILD_SONG_FAV)
        startActivity(intent)
    }

    private fun updateProfile() {
        val intent = Intent(this@MainActivity, ProfileActivity::class.java)
        startActivity(intent)
    }
    private fun playVideoFav(){
        val intent = Intent(this@MainActivity, PlayVideoActivity::class.java)
        intent.putExtra(NAME_INTENT_CHECK_VIDEO, VAL_INTENT_VIDEO_FAV)
        startActivity(intent)
    }

    private fun setNav(loginWith: String) {
        val headerView: View = binding.navMa.getHeaderView(0)
        ivAvatarNav = headerView.findViewById(R.id.cir_iv_avatar_nav)
        tvUsernameNav = headerView.findViewById(R.id.tv_username_nav)

        userViewModel.getUser()
        userViewModel.user.observe(this, Observer { user ->
            user?.let {
                tvUsernameNav.text = it.name
                if (user.image != "") {
                    Glide.with(this@MainActivity).load(it.image).into(ivAvatarNav)
                }
            } ?: run {

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

    private fun getTime(): String {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        return String.format("%02d:%02d", hour, minute)
    }

    override fun onResume() {
        super.onResume()
        val query = FirebaseDatabase.getInstance().getReference("$CHILD_DAY_OF_USE/$dayOfUseId/$CHILD_USED_MINUTE")
        query.setValue(ServerValue.increment(calculateTime(time,getTime())))
        time=getTime()
    }

    private fun calculateTime(start: String, end: String): Long {
        val format = SimpleDateFormat("HH:mm")
        val startTime = format.parse(start)
        val endTime = format.parse(end)
        val diff = (endTime?.time ?: 0) - (startTime?.time ?: 0)
        return TimeUnit.MILLISECONDS.toMinutes(diff)
    }
}
