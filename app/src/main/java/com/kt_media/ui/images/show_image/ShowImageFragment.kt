package com.kt_media.ui.images.show_image

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.kt_media.ui.images.PlayVideoFragment

class ShowImageFragment : Fragment() {
    private var videoUrl: String? = null
    companion object {
        fun newInstance(videoUrl: String): PlayVideoFragment {
            val fragment = PlayVideoFragment()
            val args = Bundle()
            args.putString("videoUrl", videoUrl)
            fragment.arguments = args
            return fragment
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //        arguments?.let {
//            videoUrl = it.getString("videoUrl")
//        }
//        binding?.tvNamePvf?.text=videoUrl
    }
}