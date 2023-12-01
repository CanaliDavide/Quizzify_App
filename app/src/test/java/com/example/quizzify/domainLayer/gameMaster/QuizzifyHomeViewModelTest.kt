package com.example.quizzify.domainLayer.gameMaster

import com.example.quizzify.MainDispatcherRule
import com.example.quizzify.dataLayer.common.Resource
import com.example.quizzify.dataLayer.database.data.Quiz
import com.example.quizzify.domainLayer.dbUseCase.GetGameTypeUseCase
import com.example.quizzify.domainLayer.dbUseCase.GetQuizzesUseCase
import com.example.quizzify.ui.composable.GameType
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class QuizzifyHomeViewModelTest {
    private val dispatcher = UnconfinedTestDispatcher()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(dispatcher)

    private val getQuizzesUseCase: GetQuizzesUseCase = mockk()
    private val getGameTypeUseCase: GetGameTypeUseCase = mockk()
    private val viewModel: QuizzifyHomeViewModel = QuizzifyHomeViewModel(getQuizzesUseCase, getGameTypeUseCase, dispatcher)

    @Before
    fun setUp() {
        clearAllMocks()
    }

    @Test
    fun `fetchQuizzes should update uiState with quiz types`() = runTest {
        val playlistQuizType = arrayListOf(
            GameType(
                title = "Playlist 1",
                description = "Playlist description",
                routeImage = "Playlist image",
                numberOfQuestions = 15
            ),
            GameType(
                title = "Playlist 2",
                description = "Playlist description",
                routeImage = "Playlist image",
                numberOfQuestions = 20
            )
        )
        val artistQuizType = arrayListOf(
            GameType(
                title = "Artist 1",
                description = "Artist description",
                routeImage = "Artist image",
                numberOfQuestions = 15
            ),
            GameType(
                title = "Artist 2",
                description = "Artist description",
                routeImage = "Artist image",
                numberOfQuestions = 20
            )
        )

        val albumQuizType = arrayListOf(
            GameType(
                title = "Album 1",
                description = "Album description",
                routeImage = "Album image",
                numberOfQuestions = 15
            ),
            GameType(
                title = "Album 2",
                description = "Album description",
                routeImage = "Album image",
                numberOfQuestions = 20
            )
        )

        val onlineQuizType = arrayListOf(
            GameType(
                title = "Online 1",
                description = "Online description",
                routeImage = "Online image",
                numberOfQuestions = 15
            )
        )

        val endlessQuizType = arrayListOf(
            GameType(
                title = "Endless 1",
                description = "Endless description",
                routeImage = "Endless image",
                numberOfQuestions = 15
            )
        )

        // Mock the behavior of getGameTypeUseCase
        coEvery { getGameTypeUseCase("PLAYLIST") } returns flowOf(Resource.Loading(), Resource.Success(playlistQuizType))
        coEvery { getGameTypeUseCase("ARTIST") } returns flowOf(Resource.Loading(), Resource.Success(artistQuizType))
        coEvery { getGameTypeUseCase("ALBUM") } returns flowOf(Resource.Loading(), Resource.Success(albumQuizType))
        coEvery { getGameTypeUseCase("ONLINE") } returns flowOf(Resource.Loading(), Resource.Success(onlineQuizType))
        coEvery { getGameTypeUseCase("ENDLESS") } returns flowOf(Resource.Loading(), Resource.Success(endlessQuizType))


        // Call the function to test
        viewModel.fetchQuizzes()


        // Verify that the uiState was updated correctly
        assertEquals(playlistQuizType, viewModel.uiState.value.playlist_quizType)
        assertEquals(artistQuizType, viewModel.uiState.value.artist_quizType)
        assertEquals(albumQuizType, viewModel.uiState.value.album_quizType)
        assertEquals(onlineQuizType[0], viewModel.uiState.value.online_quizType)
        assertEquals(endlessQuizType[0], viewModel.uiState.value.endless_quizType)
    }

    @Test
    fun `fetchQuizzes should not update uiState with error`() = runTest {
        // Mock the behavior of getGameTypeUseCase
        val error = "Error"
        coEvery { getGameTypeUseCase("PLAYLIST") } returns flowOf(Resource.Error(error))
        coEvery { getGameTypeUseCase("ARTIST") } returns flowOf(Resource.Error(error))
        coEvery { getGameTypeUseCase("ALBUM") } returns flowOf(Resource.Error(error))
        coEvery { getGameTypeUseCase("ONLINE") } returns flowOf(Resource.Error(error))
        coEvery { getGameTypeUseCase("ENDLESS") } returns flowOf(Resource.Error(error))

        // Call the function to test
        viewModel.fetchQuizzes()

        // Verify that the uiState still contains default values
        assertEquals(arrayListOf<GameType>(), viewModel.uiState.value.playlist_quizType)
        assertEquals(arrayListOf<GameType>(), viewModel.uiState.value.artist_quizType)
        assertEquals(arrayListOf<GameType>(), viewModel.uiState.value.album_quizType)
        assertEquals(GameType(), viewModel.uiState.value.online_quizType)
        assertEquals(GameType(), viewModel.uiState.value.endless_quizType)
    }

    @Test
    fun `fetchCompletedQuizzes should update uiState with completed quizzes`(){
        val quizzesArtist = arrayListOf(
            Quiz(
                collection = "Artist Quiz 1",
                id = "1",
                completed = true
            ),
            Quiz(
                collection = "Artist Quiz 2",
                id = "2",
                completed = true
            )
        )

        val quizzesPlaylist = arrayListOf(
            Quiz(
                collection = "Playlist Quiz 1",
                id = "1",
                completed = true
            )
        )

        val quizzesAlbum = arrayListOf(
            Quiz(
                collection = "Album Quiz 1",
                id = "1",
                completed = true
            )
        )

        //Mock the behavior of getQuizzesUseCase
        coEvery { getQuizzesUseCase("ARTIST") } returns flowOf(Resource.Loading(), Resource.Success(quizzesArtist))
        coEvery { getQuizzesUseCase("PLAYLIST") } returns flowOf(Resource.Loading(), Resource.Success(quizzesPlaylist))
        coEvery { getQuizzesUseCase("ALBUM") } returns flowOf(Resource.Loading(), Resource.Success(quizzesAlbum))

        // Call the function to test
        viewModel.fetchCompletedQuizzes()

        // Verify that the uiState was updated correctly
        assertEquals(quizzesArtist, viewModel.uiState.value.quizzes_artist)
        assertEquals(quizzesPlaylist, viewModel.uiState.value.quizzes_playlist)
        assertEquals(quizzesAlbum, viewModel.uiState.value.quizzes_album)

        // Verify that the isCompleted function works correctly
        viewModel.setCategory("ARTIST")
        assertTrue(viewModel.isCompleted("1"))
        assertTrue(viewModel.isCompleted("2"))

        viewModel.setCategory("PLAYLIST")
        assertTrue(viewModel.isCompleted("1"))
        assertFalse(viewModel.isCompleted("OTHER ID"))

        viewModel.setCategory("ALBUM")
        assertTrue(viewModel.isCompleted("1"))
        assertFalse(viewModel.isCompleted("OTHER ID"))

        viewModel.setCategory("OTHER TAG")
        assertFalse(viewModel.isCompleted("OTHER TAG"))
    }

    @Test
    fun `fetchCompletedQuizzes should not update uiState with error`() {
        // Mock the behavior of getQuizzesUseCase
        val error = "Error"
        coEvery { getQuizzesUseCase("ARTIST") } returns flowOf(Resource.Error(error))
        coEvery { getQuizzesUseCase("PLAYLIST") } returns flowOf(Resource.Error(error))
        coEvery { getQuizzesUseCase("ALBUM") } returns flowOf(Resource.Error(error))

        // Call the function to test
        viewModel.fetchCompletedQuizzes()

        // Verify that the uiState still contains default values
        assertEquals(arrayListOf<Quiz>(), viewModel.uiState.value.quizzes_artist)
        assertEquals(arrayListOf<Quiz>(), viewModel.uiState.value.quizzes_playlist)
        assertEquals(arrayListOf<Quiz>(), viewModel.uiState.value.quizzes_album)
    }

    @Test
    fun `setCategory should update uiState with selected category`(){
        // Call the function to test
        viewModel.setCategory("ARTIST")

        // Verify that the uiState was updated correctly
        assertEquals("ARTIST", viewModel.uiState.value.currentCategory)
    }

}