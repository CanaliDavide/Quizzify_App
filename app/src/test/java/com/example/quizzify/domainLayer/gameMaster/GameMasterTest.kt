package com.example.quizzify.domainLayer.gameMaster

import com.example.quizzify.MainDispatcherRule
import com.example.quizzify.dataLayer.common.Resource
import com.example.quizzify.dataLayer.spotify.data.TopArtists
import com.example.quizzify.dataLayer.spotify.data.TopTracks
import com.example.quizzify.dataLayer.spotify.data.UserAlbums
import com.example.quizzify.dataLayer.spotify.data.base.Album
import com.example.quizzify.dataLayer.spotify.data.base.Artist
import com.example.quizzify.dataLayer.spotify.data.base.Track
import com.example.quizzify.domainLayer.dbUseCase.IsToSaveUseCase
import com.example.quizzify.domainLayer.dbUseCase.UpdateEndlessUseCase
import com.example.quizzify.domainLayer.dbUseCase.UpdateOnlineUseCase
import com.example.quizzify.domainLayer.dbUseCase.UpdateQuizUseCase
import com.example.quizzify.domainLayer.question.DescriptionArtistQuestion
import com.example.quizzify.domainLayer.question.DescriptionSongQuestion
import com.example.quizzify.domainLayer.question.LyricsSongMXMQuestion
import com.example.quizzify.domainLayer.question.NumberAlbumArtistQuestion
import com.example.quizzify.domainLayer.useCase.GetAlbumFromArtistsUseCase
import com.example.quizzify.domainLayer.useCase.GetAlbumFromPlaylistUseCase
import com.example.quizzify.domainLayer.useCase.GetArtistFromPlaylistUseCase
import com.example.quizzify.domainLayer.useCase.GetPlaylistUseCase
import com.example.quizzify.domainLayer.useCase.GetRecommendationsAlbumsUseCase
import com.example.quizzify.domainLayer.useCase.GetRecommendationsArtistsUseCase
import com.example.quizzify.domainLayer.useCase.GetRecommendationsTracksUseCase
import com.example.quizzify.domainLayer.useCase.GetTopArtistsUseCase
import com.example.quizzify.domainLayer.useCase.GetTopSongsUseCase
import com.example.quizzify.domainLayer.useCase.GetUserAlbumsUseCase
import com.example.quizzify.ui.composable.GameType
import com.example.quizzify.domainLayer.utils.QuizTimer
import kotlin.time.Duration.Companion.milliseconds
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*

import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

class GameMasterTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getTopArtistsUseCase: GetTopArtistsUseCase = mockk()
    private val getTopSongsUseCase: GetTopSongsUseCase = mockk()
    private val descriptionArtistQuestion: DescriptionArtistQuestion = mockk()
    private val numberAlbumArtistQuestion: NumberAlbumArtistQuestion = mockk()
    private val getUserAlbumsUseCase: GetUserAlbumsUseCase = mockk()
    private val descriptionSongQuestion: DescriptionSongQuestion = mockk()
    private val lyricsSongMXMQuestion: LyricsSongMXMQuestion = mockk()
    private val getRecommendationsArtistsUseCase: GetRecommendationsArtistsUseCase = mockk()
    private val getRecommendationsAlbumsUseCase: GetRecommendationsAlbumsUseCase = mockk()
    private val getRecommendationsTracksUseCase: GetRecommendationsTracksUseCase = mockk()
    private val updateQuizUseCase: UpdateQuizUseCase = mockk()
    private val updateEndlessUseCase: UpdateEndlessUseCase = mockk()
    private val isToSaveUseCase: IsToSaveUseCase = mockk()
    private val getArtistFromPlaylistUseCase: GetArtistFromPlaylistUseCase = mockk()
    private val getPlaylistUseCase: GetPlaylistUseCase = mockk()
    private val getAlbumFromPlaylistUseCase: GetAlbumFromPlaylistUseCase = mockk()
    private val getAlbumFromArtistsUseCase: GetAlbumFromArtistsUseCase = mockk()
    private val updateOnlineUseCase: UpdateOnlineUseCase = mockk()

    private val gameMaster = GameMaster(
        getTopArtistsUseCase,
        getTopSongsUseCase,
        descriptionArtistQuestion,
        numberAlbumArtistQuestion,
        getUserAlbumsUseCase,
        descriptionSongQuestion,
        lyricsSongMXMQuestion,
        getRecommendationsArtistsUseCase,
        getRecommendationsAlbumsUseCase,
        getRecommendationsTracksUseCase,
        updateQuizUseCase,
        updateEndlessUseCase,
        isToSaveUseCase,
        getArtistFromPlaylistUseCase,
        getPlaylistUseCase,
        getAlbumFromPlaylistUseCase,
        getAlbumFromArtistsUseCase,
        updateOnlineUseCase
    )

    companion object {
        @JvmStatic
        @BeforeClass
        fun setUpClass(): Unit {
            mockkObject(QuizTimer)
        }
    }


    @Before
    fun setUp() {
        clearAllMocks()
    }

    @Test
    fun isLastQuestion() {
        val questions = arrayListOf<Question>()
        for (i in 1..10) {
            questions.add(
                Question(
                    right = 1,
                    question = "question$i",
                    answers = arrayListOf("right", "wrong1", "wrong2", "wrong3"),
                    // time to parse is a duration
                    timeToAnswer = 10.seconds,
                )
            )
        }

        val gameState = GameState(
            questions = questions,
            currentQuizId = 0,
        )

        gameMaster.setStateGame(
            gameState
        )

        assertFalse(gameMaster.isLastQuestion())

        gameMaster.setStateGame(
            gameState.copy(
                currentQuizId = 9
            )
        )

        assertTrue(gameMaster.isLastQuestion())
    }

    @Test
    fun testTimer() {
        //Mocking quizTimer to return a flow of 1
        every { QuizTimer.tick } returns flowOf(1.0f)
        val questions = arrayListOf<Question>()
        for (i in 1..10) {
            questions.add(
                Question(
                    right = 1,
                    question = "question$i",
                    answers = arrayListOf("right", "wrong1", "wrong2", "wrong3"),
                    // time to parse is a duration
                    timeToAnswer = 10.seconds,
                )
            )
        }

        val gameState = GameState(
            questions = questions,
            currentQuizId = 0,
        )

        gameMaster.setStateGame(
            gameState
        )

        gameMaster.startTimer(duration = 100.milliseconds)
        assertTrue(gameMaster.stateGame.value.isAnswered)
        assertEquals(1.0f, gameMaster.stateTimer.value.time)
        gameMaster.resetTimer()
        assertEquals(0.0f, gameMaster.stateTimer.value.time)
        assertFalse(gameMaster.stateTimer.value.active)
    }


    /*
    @Test
    fun startTimer() {

        //Mocking quizTimer to return a flow of 1
        every { QuizTimer.tick } returns flowOf(1.0f)
        gameMaster.startTimer(duration = 100.milliseconds)
        assertTrue(gameMaster.stateGame.value.isAnswered)

    }
    */


    @Test
    fun setGameType() {
        val gameType = GameType(routeImage = "test")
        gameMaster.setGameType(gameType)
        assertEquals(gameType, gameMaster.stateGame.value.gameType)
    }

    // Helper to create mock artists
    private fun createMockArtists(number: Int =5):
            List<Artist> {
        val artists = mutableListOf<Artist>()
        for (i in 1..number) {
            artists.add(
                Artist(
                    genres = arrayListOf("Pop", "Rock"),
                    images = arrayListOf("https://example.com/artist$i.jpg"),
                    name = "Artist $i",
                    id = "artist$i"
                )
            )
        }
        return artists
    }

    // Helper to create mock tracks
    private fun createMockTracks(artists: List<Artist>):
            List<Track> {
        val tracks = mutableListOf<Track>()
        for (i in 1..artists.size) {
            tracks.add(
                Track(
                    id = "track$i",
                    artists = arrayListOf(artists[i - 1]),
                    title = "Track $i",
                    preview_url = "https://example.com/track$i"
                )
            )
        }
        return tracks
    }

    // Helper to create mock albums
    private fun createMockAlbums(artists: List<Artist>, tracks: List<Track>):
            List<Album> {
        val albums = mutableListOf<Album>()
        for (i in 1..artists.size) {
            albums.add(
                Album(
                    id = "album$i",
                    title = "Album $i",
                    totalTracks = tracks.size.toLong(),
                    releaseDate = "2021-01-01",
                    artists = arrayListOf(artists[i - 1]),
                    images = arrayListOf("https://example.com/album$i.jpg"),
                    tracks = ArrayList(tracks)
                )
            )
        }
        return albums
    }

    // Helper to mock the use cases
    private fun mockUseCases(
        artists: List<Artist>,
        tracks: List<Track>,
        albums: List<Album>,
    ) {
        val topArtist = TopArtists(total = artists.size, artists = arrayListOf(artists[0]))
        val topTracks = arrayListOf(tracks[0])
        val userAlbums = UserAlbums(
            total = 1,
            albums = arrayListOf(albums[0])
        )

        val recommendationsTracks = arrayListOf(tracks[1], tracks[2], tracks[3], tracks[4])
        val topSongsResponse = Resource.Success(TopTracks(total = topTracks.size, tracks = topTracks))
        val recommendationsTrackResponse = Resource.Success(recommendationsTracks)

        // Mocking the use cases
        coEvery { getTopSongsUseCase(any(), any(), any()) } returns flowOf(topSongsResponse)
        coEvery { getUserAlbumsUseCase(any(), any()) } returns flowOf(Resource.Loading(), Resource.Success(userAlbums))
        coEvery { getRecommendationsAlbumsUseCase(any(), any(), any(), any()) } returns flowOf(Resource.Loading(), Resource.Success(
            arrayListOf(albums[1], albums[2], albums[3], albums[4])
        ))
        coEvery { getAlbumFromArtistsUseCase(any()) } returns flowOf(Resource.Loading(), Resource.Success(albums))
        coEvery { getAlbumFromPlaylistUseCase(any()) } returns flowOf(Resource.Loading(), Resource.Success(albums))

        coEvery {getTopArtistsUseCase(any(), any(), any())} returns flowOf(Resource.Loading(), Resource.Success(topArtist))
        coEvery { getRecommendationsArtistsUseCase(any(), any(), any(), any()) } returns flowOf(Resource.Loading(), Resource.Success(
            arrayListOf(artists[1], artists[2], artists[3], artists[4])
        ))
        coEvery { getArtistFromPlaylistUseCase(any()) } returns flowOf(Resource.Loading(), Resource.Success(artists))
        coEvery { getPlaylistUseCase(any()) } returns flowOf(Resource.Loading(), Resource.Success(tracks))

        coEvery{isToSaveUseCase(any(), any())} returns flowOf(Resource.Loading(), Resource.Success(true))
        coEvery { getRecommendationsTracksUseCase(any(), any(), any(), any()) } returns flowOf(recommendationsTrackResponse)

        coEvery { numberAlbumArtistQuestion(any()) } returns flowOf(Resource.Loading(), Resource.Success(10))
        coEvery { lyricsSongMXMQuestion(any(), any()) } returns flowOf(Resource.Loading(), Resource.Success("Lyrics"))
        coEvery { descriptionArtistQuestion(any()) } returns flowOf(Resource.Loading(), Resource.Success("Description"))
        coEvery { descriptionSongQuestion(any(), any()) } returns flowOf(Resource.Loading(), Resource.Success("Description"))
    }

    private fun mockUseCasesError(){
        coEvery { getTopSongsUseCase(any(), any(), any()) } returns flowOf(Resource.Error("Error"))
        coEvery { getUserAlbumsUseCase(any(), any()) } returns flowOf(Resource.Error("Error"))
        coEvery { getRecommendationsAlbumsUseCase(any(), any(), any(), any()) } returns flowOf(Resource.Error("Error"))
        coEvery { getAlbumFromArtistsUseCase(any()) } returns flowOf(Resource.Error("Error"))
        coEvery { getTopArtistsUseCase(any(), any(), any()) } returns flowOf(Resource.Error("Error"))
        coEvery { getRecommendationsArtistsUseCase(any(), any(), any(), any()) } returns flowOf(Resource.Error("Error"))
        coEvery { getArtistFromPlaylistUseCase(any()) } returns flowOf(Resource.Error("Error"))
        coEvery { getPlaylistUseCase(any()) } returns flowOf(Resource.Error("Error"))
        coEvery { isToSaveUseCase(any(), any()) } returns flowOf(Resource.Error("Error"))
        coEvery { getRecommendationsTracksUseCase(any(), any(), any(), any()) } returns flowOf(Resource.Error("Error"))
        coEvery { numberAlbumArtistQuestion(any()) } returns flowOf(Resource.Error("Error"))
        coEvery { lyricsSongMXMQuestion(any(), any()) } returns flowOf(Resource.Error("Error"))
        coEvery { descriptionArtistQuestion(any()) } returns flowOf(Resource.Error("Error"))
        coEvery { descriptionSongQuestion(any(), any()) } returns flowOf(Resource.Error("Error"))
    }



    @Test
    fun `fetchQuestions with category ENDLESS`() {
        // Creating mock data
        val artists = createMockArtists(5)
        val tracks = createMockTracks(artists)
        val albums = createMockAlbums(artists, tracks)

        // Mocking the use cases
        mockUseCases(artists, tracks, albums)

        // Setting the game type
        val gameType = GameType(routeImage = "test", category = "ENDLESS")
        gameMaster.setGameType(gameType)

        // Fetching the questions
        gameMaster.fetchQuestions()

        // Checking the state of the game
        assertNotEquals(0, gameMaster.stateGame.value.questions.size)
        assertFalse(gameMaster.stateGame.value.error_occurred)
        assertFalse(gameMaster.stateGame.value.isAnswered)
        assertEquals(0, gameMaster.stateGame.value.score)
        assertEquals(gameType, gameMaster.stateGame.value.gameType)
        assertEquals(0, gameMaster.stateGame.value.currentQuizId)

        //Answering the first question
        gameMaster.answerClicked(gameMaster.stateGame.value.questions[0].right)
        assertTrue(gameMaster.stateGame.value.isAnswered)
        assertEquals(1, gameMaster.stateGame.value.score)
        assertFalse(gameMaster.stateGame.value.isFailed)
    }

    @Test
    fun `fetchQuestions with category ENDLESS with error`(){
        // Mocking the use cases
        mockUseCasesError()

        // Setting the game type
        val gameType = GameType(routeImage = "test", category = "ENDLESS")
        gameMaster.setGameType(gameType)

        // Fetching the questions
        gameMaster.fetchQuestions()

        // Checking the state of the game
        assertEquals(0, gameMaster.stateGame.value.questions.size)
        assertTrue(gameMaster.stateGame.value.error_occurred)
        assertEquals("Error", gameMaster.stateGame.value.error_message)
        assertFalse(gameMaster.stateGame.value.isAnswered)
        assertEquals(0, gameMaster.stateGame.value.score)
        assertEquals(gameType, gameMaster.stateGame.value.gameType)
        assertEquals(0, gameMaster.stateGame.value.currentQuizId)
    }

    @Test
    fun `fetchQuestions with category ARTIST and keyword MYTOPARTIST`() {
        val artists = createMockArtists(5)
        val tracks = createMockTracks(artists)
        val albums = createMockAlbums(artists, tracks)

        // Mocking the use cases
        mockUseCases(artists, tracks, albums)

        // Setting the game type
        val gameType = GameType(routeImage = "test", category = "ARTIST", keyword = "MYTOPARTIST")
        gameMaster.setGameType(gameType)
        gameMaster.fetchQuestions()

        // Checking the state of the game
        assertNotEquals(0, gameMaster.stateGame.value.questions.size)
        assertFalse(gameMaster.stateGame.value.error_occurred)
        assertFalse(gameMaster.stateGame.value.isAnswered)
        assertEquals(0, gameMaster.stateGame.value.score)
        assertEquals(gameType, gameMaster.stateGame.value.gameType)
        assertEquals(0, gameMaster.stateGame.value.currentQuizId)

        //Answering the first question
        gameMaster.answerClicked(gameMaster.stateGame.value.questions[0].right)
        assertTrue(gameMaster.stateGame.value.isAnswered)
        assertEquals(1, gameMaster.stateGame.value.score)
        assertFalse(gameMaster.stateGame.value.isFailed)
    }

    @Test
    fun `fetchQuestions with category ARTIST and error`(){
        // Mocking the use cases
        mockUseCasesError()

        // Setting the game type
        val gameType = GameType(routeImage = "test", category = "ARTIST")
        gameMaster.setGameType(gameType)
        gameMaster.fetchQuestions()

        // Checking the state of the game
        assertEquals(0, gameMaster.stateGame.value.questions.size)
        assertTrue(gameMaster.stateGame.value.error_occurred)
        assertEquals("Error", gameMaster.stateGame.value.error_message)
        assertFalse(gameMaster.stateGame.value.isAnswered)
        assertEquals(0, gameMaster.stateGame.value.score)
        assertEquals(gameType, gameMaster.stateGame.value.gameType)
        assertEquals(0, gameMaster.stateGame.value.currentQuizId)
    }

    @Test
    fun `fetchQuestions with category ARTIST`() {
        val artists = createMockArtists(5)
        val tracks = createMockTracks(artists)
        val albums = createMockAlbums(artists, tracks)

        // Mocking the use cases
        mockUseCases(artists, tracks, albums)

        // Setting the game type
        val gameType = GameType(routeImage = "test", category = "ARTIST")
        gameMaster.setGameType(gameType)
        gameMaster.fetchQuestions()

        // Checking the state of the game
        assertNotEquals(0, gameMaster.stateGame.value.questions.size)
        assertFalse(gameMaster.stateGame.value.error_occurred)
        assertFalse(gameMaster.stateGame.value.isAnswered)
        assertEquals(0, gameMaster.stateGame.value.score)
        assertEquals(gameType, gameMaster.stateGame.value.gameType)
        assertEquals(0, gameMaster.stateGame.value.currentQuizId)

        //Answering the first question
        gameMaster.answerClicked(gameMaster.stateGame.value.questions[0].right)
        assertTrue(gameMaster.stateGame.value.isAnswered)
        assertEquals(1, gameMaster.stateGame.value.score)
        assertFalse(gameMaster.stateGame.value.isFailed)
    }

    @Test
    fun `fetchQuestions with category ALBUM and keyword MYTOPALBUM`() {
        val artists = createMockArtists(5)
        val tracks = createMockTracks(artists)
        val albums = createMockAlbums(artists, tracks)

        // Mocking the use cases
        mockUseCases(artists, tracks, albums)

        // Setting the game type
        val gameType = GameType(routeImage = "test", category = "ALBUM", keyword = "MYTOPALBUM")
        gameMaster.setGameType(gameType)
        gameMaster.fetchQuestions()

        // Checking the state of the game
        assertNotEquals(0, gameMaster.stateGame.value.questions.size)
        assertFalse(gameMaster.stateGame.value.error_occurred)
        assertFalse(gameMaster.stateGame.value.isAnswered)
        assertEquals(0, gameMaster.stateGame.value.score)
        assertEquals(gameType, gameMaster.stateGame.value.gameType)
        assertEquals(0, gameMaster.stateGame.value.currentQuizId)

        //Answering the first question
        gameMaster.answerClicked(gameMaster.stateGame.value.questions[0].right)
        assertTrue(gameMaster.stateGame.value.isAnswered)
        assertEquals(1, gameMaster.stateGame.value.score)
        assertFalse(gameMaster.stateGame.value.isFailed)
    }

    @Test
    fun `fetchQuestions with category ALBUM`() {
        val artists = createMockArtists(40)
        val tracks = createMockTracks(artists)
        val albums = createMockAlbums(artists, tracks)

        // Mocking the use cases
        mockUseCases(artists, tracks, albums)

        // Setting the game type
        val gameType = GameType(routeImage = "test", category = "ALBUM", numberOfQuestions = 30)
        gameMaster.setGameType(gameType)
        gameMaster.fetchQuestions()

        // Checking the state of the game
        assertNotEquals(0, gameMaster.stateGame.value.questions.size)
        assertFalse(gameMaster.stateGame.value.error_occurred)
        assertFalse(gameMaster.stateGame.value.isAnswered)
        assertEquals(0, gameMaster.stateGame.value.score)
        assertEquals(gameType, gameMaster.stateGame.value.gameType)
        assertEquals(0, gameMaster.stateGame.value.currentQuizId)

        //Answering the first question
        gameMaster.answerClicked(gameMaster.stateGame.value.questions[0].right)
        assertTrue(gameMaster.stateGame.value.isAnswered)
        assertEquals(1, gameMaster.stateGame.value.score)
        assertFalse(gameMaster.stateGame.value.isFailed)
    }

    @Test
    fun `fetchQuestions with category ALBUM and playlist ID`() {
        val artists = createMockArtists(5)
        val tracks = createMockTracks(artists)
        val albums = createMockAlbums(artists, tracks)

        // Mocking the use cases
        mockUseCases(artists, tracks, albums)

        // Setting the game type
        val gameType = GameType(routeImage = "test", category = "ALBUM", idSpotify = "test")
        gameMaster.setGameType(gameType)


        gameMaster.fetchQuestions()

        // Checking the state of the game
        assertNotEquals(0, gameMaster.stateGame.value.questions.size)
        assertFalse(gameMaster.stateGame.value.error_occurred)
        assertFalse(gameMaster.stateGame.value.isAnswered)
        assertEquals(0, gameMaster.stateGame.value.score)
        assertEquals(gameType, gameMaster.stateGame.value.gameType)
        assertEquals(0, gameMaster.stateGame.value.currentQuizId)

        //Answering the first question
        gameMaster.answerClicked(gameMaster.stateGame.value.questions[0].right)
        assertTrue(gameMaster.stateGame.value.isAnswered)
        assertEquals(1, gameMaster.stateGame.value.score)
        assertFalse(gameMaster.stateGame.value.isFailed)
    }


    @Test
    fun `fetchQuestions with category ALBUM and error`(){
        // Mocking the use cases
        mockUseCasesError()

        // Setting the game type
        val gameType = GameType(routeImage = "test", category = "ALBUM")
        gameMaster.setGameType(gameType)
        gameMaster.fetchQuestions()

        // Checking the state of the game
        assertEquals(0, gameMaster.stateGame.value.questions.size)
        assertTrue(gameMaster.stateGame.value.error_occurred)
        assertEquals("Error", gameMaster.stateGame.value.error_message)
        assertFalse(gameMaster.stateGame.value.isAnswered)
        assertEquals(0, gameMaster.stateGame.value.score)
        assertEquals(gameType, gameMaster.stateGame.value.gameType)
        assertEquals(0, gameMaster.stateGame.value.currentQuizId)
    }

    @Test
    fun `fetchQuestions with category ALBUM and keyword ALBUMFROMPARTIST`() {
        val artists = createMockArtists(5)
        val tracks = createMockTracks(artists)
        val albums = createMockAlbums(artists, tracks)

        // Mocking the use cases
        mockUseCases(artists, tracks, albums)

        // Setting the game type
        val gameType = GameType(routeImage = "test", category = "ALBUM", keyword = "ALBUMFROMPARTIST")
        gameMaster.setGameType(gameType)
        gameMaster.fetchQuestions()

        // Checking the state of the game
        assertNotEquals(0, gameMaster.stateGame.value.questions.size)
        assertFalse(gameMaster.stateGame.value.error_occurred)
        assertFalse(gameMaster.stateGame.value.isAnswered)
        assertEquals(0, gameMaster.stateGame.value.score)
        assertEquals(gameType, gameMaster.stateGame.value.gameType)
        assertEquals(0, gameMaster.stateGame.value.currentQuizId)

        //Answering the first question
        gameMaster.answerClicked(gameMaster.stateGame.value.questions[0].right)
        assertTrue(gameMaster.stateGame.value.isAnswered)
        assertEquals(1, gameMaster.stateGame.value.score)
        assertFalse(gameMaster.stateGame.value.isFailed)
    }

    @Test
    fun `fetchQuestions with category PLAYLIST`() {
        val artists = createMockArtists(5)
        val tracks = createMockTracks(artists)
        val albums = createMockAlbums(artists, tracks)

        // Mocking the use cases
        mockUseCases(artists, tracks, albums)

        // Setting the game type
        val gameType = GameType(routeImage = "test", category = "PLAYLIST")
        gameMaster.setGameType(gameType)
        gameMaster.fetchQuestions()

        // Checking the state of the game
        assertNotEquals(0, gameMaster.stateGame.value.questions.size)
        assertFalse(gameMaster.stateGame.value.error_occurred)
        assertFalse(gameMaster.stateGame.value.isAnswered)
        assertEquals(0, gameMaster.stateGame.value.score)
        assertEquals(gameType, gameMaster.stateGame.value.gameType)
        assertEquals(0, gameMaster.stateGame.value.currentQuizId)

        //Answering the first question
        gameMaster.answerClicked(gameMaster.stateGame.value.questions[0].right)
        assertTrue(gameMaster.stateGame.value.isAnswered)
        assertEquals(1, gameMaster.stateGame.value.score)
        assertFalse(gameMaster.stateGame.value.isFailed)
    }

    @Test
    fun `fetchQuestions with category PLAYLIST and error`(){
        // Mocking the use cases
        mockUseCasesError()

        // Setting the game type
        val gameType = GameType(routeImage = "test", category = "PLAYLIST")
        gameMaster.setGameType(gameType)
        gameMaster.fetchQuestions()

        // Checking the state of the game
        assertEquals(0, gameMaster.stateGame.value.questions.size)
        assertTrue(gameMaster.stateGame.value.error_occurred)
        assertEquals("Error", gameMaster.stateGame.value.error_message)
        assertFalse(gameMaster.stateGame.value.isAnswered)
        assertEquals(0, gameMaster.stateGame.value.score)
        assertEquals(gameType, gameMaster.stateGame.value.gameType)
        assertEquals(0, gameMaster.stateGame.value.currentQuizId)
    }

    @Test
    fun `fetchQuestions with category PLAYLIST and keyword MYTOPPLAYLIST`() {
        val artists = createMockArtists(5)
        val tracks = createMockTracks(artists)
        val albums = createMockAlbums(artists, tracks)

        // Mocking the use cases
        mockUseCases(artists, tracks, albums)

        // Setting the game type
        val gameType = GameType(routeImage = "test", category = "PLAYLIST", keyword = "MYTOPPLAYLIST")
        gameMaster.setGameType(gameType)
        gameMaster.fetchQuestions()

        // Checking the state of the game
        assertNotEquals(0, gameMaster.stateGame.value.questions.size)
        assertFalse(gameMaster.stateGame.value.error_occurred)
        assertFalse(gameMaster.stateGame.value.isAnswered)
        assertEquals(0, gameMaster.stateGame.value.score)
        assertEquals(gameType, gameMaster.stateGame.value.gameType)
        assertEquals(0, gameMaster.stateGame.value.currentQuizId)

        //Answering the first question
        gameMaster.answerClicked(gameMaster.stateGame.value.questions[0].right)
        assertTrue(gameMaster.stateGame.value.isAnswered)
        assertEquals(1, gameMaster.stateGame.value.score)
        assertFalse(gameMaster.stateGame.value.isFailed)
    }



    @Test
    fun nextQuiz() {
        val questions = arrayListOf<Question>()
        for (i in 1..10) {
            questions.add(
                Question(
                    right = 1,
                    question = "question$i",
                    answers = arrayListOf("right", "wrong1", "wrong2", "wrong3"),
                    // time to parse is a duration
                    timeToAnswer = 10.seconds,
                )
            )
        }

        val gameState = GameState(
            questions = questions,
            currentQuizId = 0,
        )

        gameMaster.setStateGame(
            gameState
        )

        gameMaster.nextQuiz()
        assertEquals(1, gameMaster.stateGame.value.currentQuizId)
    }

    @Test
    fun nextQuizEndless(){
        val questions = arrayListOf<Question>()
        for (i in 1..10) {
            questions.add(
                Question(
                    right = 1,
                    question = "question$i",
                    answers = arrayListOf("right", "wrong1", "wrong2", "wrong3"),
                    // time to parse is a duration
                    timeToAnswer = 10.seconds,
                )
            )
        }

        val gameState = GameState(
            questions = questions,
            currentQuizId = 0,
            gameType = GameType(category = "ENDLESS")
        )

        gameMaster.setStateGame(
            gameState
        )

        gameMaster.nextQuiz()
        assertEquals(9, gameMaster.stateGame.value.questions.size)
    }

    @Test
    fun currentHistory() {
    }

    @Test
    fun wrongAnswerClicked() {
        val questions = arrayListOf<Question>()
        for (i in 1..10) {
            questions.add(
                Question(
                    right = 1,
                    question = "question$i",
                    answers = arrayListOf("right", "wrong1", "wrong2", "wrong3"),
                    // time to parse is a duration
                    timeToAnswer = 10.seconds,
                )
            )
        }

        val gameState = GameState(
            questions = questions,
            currentQuizId = 0,
        )

        gameMaster.setStateGame(
            gameState
        )

        gameMaster.answerClicked(3)
        assertTrue(gameMaster.stateGame.value.isFailed)
    }

    @Test
    fun saveQuizDb(){
        coEvery { updateQuizUseCase(any()) } returns Resource.Success(true)
        gameMaster.saveQuizDb(true)
        coVerify { updateQuizUseCase(any()) }
        confirmVerified(updateQuizUseCase)
    }

    @Test
    fun setReady() {
        gameMaster.setReady(true)
        assertFalse(gameMaster.stateGame.value.isAnswered)
        assertTrue(gameMaster.stateGame.value.readyToStart)
    }

    @Test
    fun saveEndlessDb() {
        coEvery { updateEndlessUseCase(any()) } returns Resource.Success(true)
        gameMaster.saveEndlessDb()
        coVerify { updateEndlessUseCase(any()) }
        confirmVerified(updateEndlessUseCase)
    }
}