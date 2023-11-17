package com.kt_media.ui.images

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kt_media.R
import com.kt_media.databinding.FragmentImagesBinding
import com.kt_media.domain.constant.NAME_INTENT_ALBUM_ID
import com.kt_media.ui.images.show_image.ShowImageActivity
import com.mymusic.ui.adapters.AlbumAdapter
import com.mymusic.ui.base.BaseViewBindingFragment
import com.mymusic.utils.extention.autoCleared
import org.koin.androidx.viewmodel.ext.android.viewModel

class ImagesFragment : BaseViewBindingFragment<FragmentImagesBinding>(R.layout.fragment_images)  {
    private var albumAdapter by autoCleared<AlbumAdapter>()

    private val imageViewModel: ImageViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding=FragmentImagesBinding.bind(view)
        albumAdapter=AlbumAdapter(onItemCategoryImageClick)
        getAllCategoryImageItem()
        binding?.recCategoryImageIf?.adapter=albumAdapter
        binding?.recCategoryImageIf?.layoutManager =
            GridLayoutManager(requireContext(), 3,RecyclerView.VERTICAL, false)

    }
    private fun getAllCategoryImageItem() {
        imageViewModel.getAllAlbumList()
        imageViewModel.allAlbumList.observe(viewLifecycleOwner, Observer { allAlbumList ->
            if (allAlbumList.isNotEmpty()) {
                albumAdapter?.submit(allAlbumList)
            }
        })
    }
    private val onItemCategoryImageClick: (Int) -> Unit = {
        val intent = Intent(requireActivity(), ShowImageActivity::class.java)
        intent.putExtra(NAME_INTENT_ALBUM_ID,it)
        startActivity(intent)
    }
}