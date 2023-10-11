package com.mymusic.ui.base

import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BaseViewBindingFragment<B : ViewBinding>(contentLayoutId: Int) : Fragment(contentLayoutId) {

    protected var binding: B? = null


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}