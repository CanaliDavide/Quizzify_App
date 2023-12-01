package com.example.quizzify.di

import com.example.quizzify.dataLayer.common.GeniusApiConst
import com.example.quizzify.dataLayer.common.MxmApiConst
import com.example.quizzify.dataLayer.common.SpotifyApiConst
import com.example.quizzify.dataLayer.database.DatabaseRepository
import com.example.quizzify.dataLayer.database.DatabaseRepositoryImpl
import com.example.quizzify.dataLayer.genius.GeniusRepository
import com.example.quizzify.dataLayer.genius.GeniusRepositoryImpl
import com.example.quizzify.dataLayer.genius.dataSource.GeniusDataSource
import com.example.quizzify.dataLayer.genius.dataSource.sourceInterface.GeniusApi
import com.example.quizzify.dataLayer.lyrics.LyricsRepository
import com.example.quizzify.dataLayer.lyrics.LyricsRepositoryImpl
import com.example.quizzify.dataLayer.lyrics.dataSource.LyricsDataSource
import com.example.quizzify.dataLayer.lyrics.dataSource.sourceInterface.LyricsApi
import com.example.quizzify.dataLayer.spotify.SpotifyRepository
import com.example.quizzify.dataLayer.spotify.SpotifyRepositoryImpl
import com.example.quizzify.dataLayer.spotify.dataSource.SpotifyDataSource
import com.example.quizzify.dataLayer.spotify.dataSource.sourceInterface.SpotifyApi
import com.example.quizzify.domainLayer.useCase.GetTopArtistsUseCase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
annotation class GeniusClient

@Qualifier
annotation class LyricsClient

@Qualifier
annotation class SpotifyClient

/**
 * Class used for dependency injection
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebase(): FirebaseFirestore {
        return Firebase.firestore
    }


    @Provides
    @Singleton
    fun provideDatabaseRepository(db: FirebaseFirestore): DatabaseRepository {
        return DatabaseRepositoryImpl(db)
    }


    @Provides
    @Singleton
    fun provideSpotifyApi(
        @SpotifyClient clientSpotify: HttpClient
    ): SpotifyApi {
        return SpotifyDataSource(clientSpotify)
    }

    @Provides
    @Singleton
    fun provideSpotifyRepository(api: SpotifyApi): SpotifyRepository {
        return SpotifyRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideGeniusApi(
        @GeniusClient clientGenius: HttpClient
    ): GeniusApi {
        return GeniusDataSource(clientGenius)
    }

    @Provides
    @Singleton
    fun provideGeniusRepository(api: GeniusApi): GeniusRepository {
        return GeniusRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideLyricsApi(
        @LyricsClient clientLyrics: HttpClient
    ): LyricsApi {
        return LyricsDataSource(clientLyrics)
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

    @Provides
    @Singleton
    @GeniusClient
    fun provideGeniusClient(): HttpClient {
        return GeniusApiConst.client
    }

    @Provides
    @Singleton
    @LyricsClient
    fun provideLyricsClient(): HttpClient {
        return MxmApiConst.client
    }

    @Provides
    @Singleton
    @SpotifyClient
    fun provideSpotifyClient(): HttpClient {
        return SpotifyApiConst.client
    }
}