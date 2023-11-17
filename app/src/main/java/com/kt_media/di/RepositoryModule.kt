package com.kt_media.di

import com.kt_media.data.repository.ArtistRepositoryImpl
import com.kt_media.data.repository.CommentRepositoryImpl
import com.kt_media.data.repository.DayOfUseRepositoryImpl
import com.kt_media.data.repository.GenreRepositoryImpl
import com.kt_media.data.repository.ImageRepositoryImpl
import com.kt_media.data.repository.UserRepositoryImpl
import com.kt_media.data.repository.VideoRepositoryImpl
import com.kt_media.domain.repository.ArtistRepository
import com.kt_media.domain.repository.CommentRepository
import com.kt_media.domain.repository.DayOfUseRepository
import com.kt_media.domain.repository.GenreRepository
import com.kt_media.domain.repository.ImageRepository
import com.kt_media.domain.repository.UserRepository
import com.kt_media.domain.repository.VideoRepository
import com.kt_media.domain.usecase.GetUserIdUseCase
import org.koin.dsl.module


val repositoryModule = module {
    single<UserRepository> {
        UserRepositoryImpl()
    }
    single<GenreRepository> {
        GenreRepositoryImpl()
    }
    single<ArtistRepository> {
        ArtistRepositoryImpl()
    }
    single<VideoRepository> {
        VideoRepositoryImpl(GetUserIdUseCase(get()))
    }
    single<ImageRepository> {
        ImageRepositoryImpl()
    }
    single<DayOfUseRepository> {
        DayOfUseRepositoryImpl(GetUserIdUseCase(get()))
    }
    single<CommentRepository> {
       CommentRepositoryImpl()
    }
}