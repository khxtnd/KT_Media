package com.kt_media.di

import com.kt_media.ui.images.ImageViewModel
import com.kt_media.ui.images.show_image.ShowImageViewModel
import com.kt_media.ui.main.MainViewModel
import com.kt_media.ui.musics.MusicViewModel
import com.kt_media.ui.statistical.StatisticalViewModel
import com.kt_media.ui.videos.VideoViewModel
import com.kt_media.ui.videos.play_video.PlayVideoViewModel
import com.mymusic.ui.adapters.GenreViewHolder
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        MainViewModel(get(),get())
    }
    viewModel {
        MusicViewModel(get(),get())
    }
    viewModel {
        VideoViewModel(get())
    }
    viewModel {
        ImageViewModel(get())
    }
    viewModel {
        StatisticalViewModel(get())
    }
    viewModel {
        ShowImageViewModel(get())
    }
    viewModel {
        PlayVideoViewModel(get(),get(),get())
    }
}