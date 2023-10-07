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
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.kt_media.R
import com.kt_media.databinding.ActivityProfileBinding
import com.kt_media.domain.entities.User
import java.io.IOException
import java.util.UUID

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var databaseReference: DatabaseReference
    private var filePath: Uri? = null
    private val PICK_IMAGE_REQUEST: Int = 2023

    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        databaseReference =
            FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.uid)

        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                binding.etUsernamePa.setText(user!!.userName)

                if (user.userImg != "") {
                    Glide.with(this@ProfileActivity).load(user.userImg).into(binding.cirIvAvatarPa)
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
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode != null) {
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
                storageReference.child("image/" + UUID.randomUUID().toString())
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
                        hashMap["userName"] = binding.etUsernamePa.text.toString()
                        hashMap["userImg"] = imageUrl

                        databaseReference.updateChildren(hashMap as Map<String, Any>)

                        binding.progressBarPa.visibility = View.GONE
                        Toast.makeText(applicationContext, "Đã tải lên", Toast.LENGTH_SHORT).show()
                        binding.btSavePa.visibility = View.GONE
                    } else {
                        binding.progressBarPa.visibility = View.GONE
                        Toast.makeText(applicationContext, "Lỗi", Toast.LENGTH_SHORT).show()
                    }
                }
        }

    }
}