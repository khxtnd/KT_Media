package com.kt_media.ui.profile

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.kt_media.databinding.ActivityProfileBinding
import com.kt_media.domain.constant.CHILD_IMAGE
import com.kt_media.domain.constant.CHILD_NAME
import com.kt_media.domain.constant.CHILD_USER
import com.kt_media.domain.constant.TITLE_ERROR
import com.kt_media.domain.constant.TITLE_SELECT_IMAGE
import com.kt_media.domain.constant.TITLE_UPLOADED
import com.kt_media.domain.constant.VAL_REQUEST_CODE
import com.kt_media.domain.entities.User
import java.io.IOException
import java.util.UUID

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var dbRefUser: DatabaseReference
    private var filePath: Uri? = null

    private lateinit var storageRef: StorageReference
    private lateinit var userId:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userId = FirebaseAuth.getInstance().currentUser?.uid.toString()
        dbRefUser =
            FirebaseDatabase.getInstance().getReference(CHILD_USER).child(userId)

        storageRef = FirebaseStorage.getInstance().reference

        dbRefUser.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                binding.etUsernamePa.setText(user!!.name)

                if (user.image != "") {
                    Glide.with(this@ProfileActivity).load(user.image).into(binding.cirIvAvatarPa)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
        binding.ivBackPa.setOnClickListener {
            finish()
        }
        binding.cirIvAvatarPa.setOnClickListener {
            chooseImage()
        }
        binding.btSavePa.setOnClickListener {
            uploadImage()
            binding.progressBarPa.visibility = View.VISIBLE
        }
    }

    private fun chooseImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, TITLE_SELECT_IMAGE), VAL_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == VAL_REQUEST_CODE && resultCode != null) {
            filePath = data!!.data
            try {
                val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                binding.cirIvAvatarPa.setImageBitmap(bitmap)
                binding.btSavePa.visibility = View.VISIBLE
            } catch (e: IOException) {

            }
        }
    }

    private fun uploadImage() {
        if (filePath != null) {
            val ref: StorageReference =
                storageRef.child("image/" + UUID.randomUUID().toString())
            val uploadTask = ref.putFile(filePath!!)
            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                ref.downloadUrl
            }
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val downloadUri = task.result
                        val imageUrl = downloadUri.toString()

                        var hashMap: HashMap<String, String> = HashMap()
                        hashMap[CHILD_NAME] = binding.etUsernamePa.text.toString()
                        hashMap[CHILD_IMAGE] = imageUrl

                        dbRefUser.updateChildren(hashMap as Map<String, Any>)

                        binding.progressBarPa.visibility = View.GONE
                        Toast.makeText(applicationContext, TITLE_UPLOADED, Toast.LENGTH_SHORT).show()
                        binding.btSavePa.visibility = View.GONE
                    } else {
                        binding.progressBarPa.visibility = View.GONE
                        Toast.makeText(applicationContext, TITLE_ERROR, Toast.LENGTH_SHORT).show()
                    }
                }
        }

    }
}