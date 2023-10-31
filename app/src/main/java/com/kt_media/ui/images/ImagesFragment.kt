package com.kt_media.ui.images

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kt_media.R
import com.kt_media.databinding.FragmentImagesBinding
import com.kt_media.domain.constant.CHILD_CATEGORY_IMAGE
import com.kt_media.domain.constant.CHILD_ID
import com.kt_media.domain.constant.CHILD_LIST_IMAGE
import com.kt_media.domain.constant.CHILD_NAME
import com.kt_media.domain.constant.NAME_INTENT_CATEGORY_IMAGE_ID
import com.kt_media.domain.entities.CategoryImage
import com.kt_media.ui.images.show_image.ShowImageActivity
import com.mymusic.ui.adapters.CategoryImageAdapter
import com.mymusic.ui.base.BaseViewBindingFragment

class ImagesFragment : BaseViewBindingFragment<FragmentImagesBinding>(R.layout.fragment_images)  {
    private lateinit var categoryImage: CategoryImageAdapter
    private var categoryImageList = arrayListOf<CategoryImage>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding=FragmentImagesBinding.bind(view)
        categoryImage=CategoryImageAdapter(onItemCategoryImageClick)
        getAllCategoryImageItem()
        binding?.recCategoryImageIf?.adapter=categoryImage
        binding?.recCategoryImageIf?.layoutManager =
            GridLayoutManager(requireContext(), 3,RecyclerView.VERTICAL, false)

    }
    private fun getAllCategoryImageItem() {
        val dbRefCategoryImage: DatabaseReference =
            FirebaseDatabase.getInstance().getReference(CHILD_CATEGORY_IMAGE)
        dbRefCategoryImage.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                categoryImageList.clear()
                for (data: DataSnapshot in dataSnapshot.children) {
                    val id= (data.child(CHILD_ID).value as Long).toInt()
                    val name = data.child(CHILD_NAME).value.toString()
                    val image = data.child(CHILD_LIST_IMAGE).children.firstOrNull()?.value.toString()
                    val categoryImage=CategoryImage(id,image,name)
                    categoryImageList.add(categoryImage)
                }
                if (categoryImageList.isNotEmpty()) {
                    categoryImage.submit(categoryImageList)
                }

            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
    private val onItemCategoryImageClick: (Int) -> Unit = {
        val intent = Intent(requireActivity(), ShowImageActivity::class.java)
        intent.putExtra(NAME_INTENT_CATEGORY_IMAGE_ID,it)
        startActivity(intent)
    }
}