package com.kt_media.di

import com.kt_media.domain.usecase.GetAllAlbumListUseCase
import com.kt_media.domain.usecase.GetAllVideoListUseCase
import com.kt_media.domain.usecase.GetArtistListUseCase
import com.kt_media.domain.usecase.GetCommentListUseCase
import com.kt_media.domain.usecase.GetDayOfUseListUseCase
import com.kt_media.domain.usecase.GetFavVideoListUseCase
import com.kt_media.domain.usecase.GetGenreListUseCase
import com.kt_media.domain.usecase.GetNameAlbumAndImageListUseCase
import com.kt_media.domain.usecase.GetTenVideoListUseCase
import com.kt_media.domain.usecase.GetUserIdUseCase
import com.kt_media.domain.usecase.GetUserUseCase
import org.koin.dsl.module

val useCaseModule = module {

    single {
        GetUserUseCase(get())
    }
    single {
        GetUserIdUseCase(get())
    }
    single {
        GetGenreListUseCase(get())
    }
    single {
        GetArtistListUseCase(get())
    }
    single {
        GetAllVideoListUseCase(get())
    }
    single {
        GetAllAlbumListUseCase(get())
    }
    single {
        GetDayOfUseListUseCase(get())
    }
    single {
        GetNameAlbumAndImageListUseCase(get())
    }
    single {
        GetTenVideoListUseCase(get())
    }
    single {
        GetFavVideoListUseCase(get())
    }
    single {
        GetCommentListUseCase(get())
    }
}