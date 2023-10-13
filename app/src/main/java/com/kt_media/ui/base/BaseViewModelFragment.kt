package com.mymusic.ui.base

import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding

abstract class BaseViewModelFragment<VM : ViewModel, B : ViewBinding>(contentLayoutId: Int) : BaseViewBindingFragment<B>(contentLayoutId) {

    protected abstract val viewModel: VM
}