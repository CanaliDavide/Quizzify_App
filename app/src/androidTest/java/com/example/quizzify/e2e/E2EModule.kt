package com.example.quizzify.e2e

import com.example.quizzify.dataLayer.database.DatabaseRepository
import com.example.quizzify.dataLayer.genius.GeniusRepository
import com.example.quizzify.dataLayer.genius.GeniusRepositoryImpl
import com.example.quizzify.dataLayer.genius.dataSource.sourceInterface.GeniusApi
import com.example.quizzify.dataLayer.lyrics.LyricsRepository
import com.example.quizzify.dataLayer.lyrics.LyricsRepositoryImpl
import com.example.quizzify.dataLayer.lyrics.dataSource.sourceInterface.LyricsApi
import com.example.quizzify.dataLayer.spotify.SpotifyRepository
import com.example.quizzify.dataLayer.spotify.SpotifyRepositoryImpl
import com.example.quizzify.dataLayer.spotify.dataSource.sourceInterface.SpotifyApi
import com.example.quizzify.di.AppModule
import com.example.quizzify.domainLayer.useCase.GetTopArtistsUseCase
import com.example.quizzify.e2e.fake.E2EFakeDatabaseRepository
import com.example.quizzify.e2e.fake.E2EFakeGeniusApi
import com.example.quizzify.e2e.fake.E2EFakeLyricsApi
import com.example.quizzify.e2e.fake.E2EFakeSpotifyApi
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton


/**
 * Class used for dependency injection
 */
@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [AppModule::class]
)
object E2EModule {

    @Provides
    @Singleton
    fun provideFirebase(): FirebaseFirestore {
        return Firebase.firestore
    }


    @Provides
    @Singleton
    fun provideDatabaseRepository(): DatabaseRepository {
        return E2EFakeDatabaseRepository()
    }


    @Provides
    @Singleton
    fun provideSpotifyApi(): SpotifyApi {
        return E2EFakeSpotifyApi()
    }

    @Provides
    @Singleton
    fun provideSpotifyRepository(api: SpotifyApi): SpotifyRepository {
        return SpotifyRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideGeniusApi(
    ): GeniusApi {
        return E2EFakeGeniusApi()
    }

    @Provides
    @Singleton
    fun provideGeniusRepository(api: GeniusApi): GeniusRepository {
        return GeniusRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideLyricsApi(): LyricsApi {
        return E2EFakeLyricsApi()
    }

    @Provides
    @Singleton
    fun provideLyricsRepository(api: LyricsApi): LyricsRepository {
        return LyricsRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideGetTopArtistUseCase(spotifyRepository: SpotifyRepository): GetTopArtistsUseCase {
        return GetTopArtistsUseCase(spotifyRepository)
    }
}