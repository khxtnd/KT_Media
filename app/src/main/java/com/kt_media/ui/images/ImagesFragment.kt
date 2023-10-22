package com.kt_media.ui.images

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kt_media.R
import com.kt_media.databinding.FragmentImagesBinding
import com.kt_media.domain.constant.CHILD_CATEGORY_IMAGE
import com.kt_media.domain.constant.CHILD_VIDEO
import com.kt_media.domain.entities.CategoryImage
import com.mymusic.ui.adapters.CategoryImageAdapter
import com.mymusic.ui.base.BaseViewBindingFragment

class ImagesFragment : BaseViewBindingFragment<FragmentImagesBinding>(R.layout.fragment_images)  {
    private lateinit var categoryImage: CategoryImageAdapter
    private var categoryImageList = arrayListOf<CategoryImage>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding=FragmentImagesBinding.bind(view)
        categoryImage=CategoryImageAdapter(onItemCategoryImageClick)
        getAllCategoryItem()
        binding?.recCategoryImageIf?.adapter=categoryImage
        binding?.recCategoryImageIf?.layoutManager =
            GridLayoutManager(requireContext(), 2,RecyclerView.VERTICAL, false)

    }
    private fun getAllCategoryItem() {
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference(CHILD_CATEGORY_IMAGE)
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                categoryImageList.clear()
                for (data: DataSnapshot in dataSnapshot.children) {
                    val categoryImage = data.getValue(CategoryImage::class.java)
                    categoryImage?.let { categoryImageList.add(it) }
                }
                if (categoryImageList.isNotEmpty()) {
                    categoryImage.submit(categoryImageList)
                }

            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }
    private val onItemCategoryImageClick: (Int) -> Unit = {
//        val intent = Intent(requireActivity(), PlaySongCategoryActivity::class.java)
//        intent.putExtra(NAME_INTENT_CHECK_CATEGORY, CHILD_GENRE)
//        intent.putExtra(NAME_INTENT_CATEGORY_ID ,it.id)
//        startActivity(intent)
    }
}