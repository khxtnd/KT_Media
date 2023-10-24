package com.kt_media.ui.images.show_image

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kt_media.databinding.ActivityShowImageBinding
import com.kt_media.domain.constant.CHILD_CATEGORY_IMAGE
import com.kt_media.domain.constant.CHILD_ID
import com.kt_media.domain.constant.CHILD_LIST_IMAGE
import com.kt_media.domain.constant.CHILD_NAME
import com.kt_media.domain.constant.NAME_INTENT_CATEGORY_IMAGE_ID
import com.kt_media.domain.constant.VAL_REQUEST_CODE
import com.mymusic.ui.adapters.ImageAdapter

class ShowImageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityShowImageBinding
    private lateinit var adapter: ImageAdapter
    private var imageList = arrayListOf<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val idCategoryImage=intent.getIntExtra(NAME_INTENT_CATEGORY_IMAGE_ID, 0)
        adapter = ImageAdapter()
        getAllImageItem(idCategoryImage)
        binding.viewPager2Sia.adapter = adapter
        binding.ivBackSia.setOnClickListener {
            finish()
        }
    }

    private fun getAllImageItem(idCategoryImage:Int) {
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference(CHILD_CATEGORY_IMAGE)
        val query =
            databaseReference.orderByChild(CHILD_ID).equalTo(idCategoryImage.toDouble())

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                imageList.clear()
                for (data in dataSnapshot.children) {
                    val categoryImage = data.child(CHILD_LIST_IMAGE).children
                    for (imageData in categoryImage) {
                        val imageUrl = imageData.value as String
                        imageList.add(imageUrl)
                    }
                    if(imageList.isNotEmpty()){
                        adapter.submit(imageList)
                    }
                    val categoryName = data.child(CHILD_NAME).value as String
                    binding.tvCategoryImageSia.text=categoryName
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }
}