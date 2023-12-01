package com.example.quizzify.domainLayer.dbUseCase

import com.example.quizzify.MainDispatcherRule
import com.example.quizzify.dataLayer.common.Resource
import com.example.quizzify.dataLayer.database.DatabaseRepository
import com.example.quizzify.dataLayer.database.data.CompetitiveResponse
import com.example.quizzify.dataLayer.database.data.EndlessQuiz
import com.example.quizzify.dataLayer.database.data.Quiz
import com.example.quizzify.dataLayer.database.data.Rank
import com.example.quizzify.ui.composable.GameType
import com.patrykandpatrick.vico.core.entry.FloatEntry
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*

import org.junit.Before
import org.junit.Rule
import org.junit.Test

class DbUseCaseTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val databaseRepository: DatabaseRepository = mockk()

    private val getEndlessUseCase: GetEndlessUseCase = GetEndlessUseCase(databaseRepository)
    private val getGameTypeUseCase: GetGameTypeUseCase = GetGameTypeUseCase(databaseRepository)
    private val getOnlineUseCase: GetOnlineUseCase = GetOnlineUseCase(databaseRepository)
    private val getQuizzesUseCase: GetQuizzesUseCase = GetQuizzesUseCase(databaseRepository)
    private val getRankingUseCase: GetRankingUseCase = GetRankingUseCase(databaseRepository)
    private val isToSaveUseCase: IsToSaveUseCase = IsToSaveUseCase(databaseRepository)
    private val updateEndlessUseCase: UpdateEndlessUseCase = UpdateEndlessUseCase(databaseRepository)
    private val updateQuizUseCase: UpdateQuizUseCase = UpdateQuizUseCase(databaseRepository)

    @Before
    fun setUp() {
        clearAllMocks()
    }

    @Test
    fun `getEndlessUseCase should return scores from database`() = runTest {
        // Mock the use case response
        val response = CompetitiveResponse(
            id = "ENDLESS", collection = "ENDLESS", maxScore = 100.0, scores = arrayListOf(
                FloatEntry(1f, 100.0f), FloatEntry(2f, 90.0f), FloatEntry(3f, 80f)
            )
        )
        coEvery { databaseRepository.getScores("Endless") } returns flowOf(Resource.Success(response))

        // Call the function to test
        val result = getEndlessUseCase()

        // Assert the correctness of the response
        assertEquals(response, result.first().data)
    }

    @Test
    fun `getGameTypeUseCase should return scores from database`() = runTest {
        // Mock the use case response
        val response = arrayListOf(
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
        coEvery { databaseRepository.getGameType(any()) } returns flowOf(Resource.Success(response))

        // Call the function to test
        val result = getGameTypeUseCase("Any string")

        // Assert the correctness of the response
        assertEquals(response, result.first().data)
    }

    @Test
    fun `getOnlineUseCase should return scores from database`() = runTest {
        // Mock the use case response
        val response = CompetitiveResponse(
            id = "ONLINE", collection = "ONLINE", maxScore = 100.0, scores = arrayListOf(
                FloatEntry(1f, 100.0f), FloatEntry(2f, 90.0f), FloatEntry(3f, 80f)
            )
        )
        coEvery { databaseRepository.getScores("Online") } returns flowOf(Resource.Success(response))

        // Call the function to test
        val result = getOnlineUseCase()

        // Assert the correctness of the response
        assertEquals(response, result.first().data)
    }

    @Test
    fun `getQuizzesUseCase should return scores from database`() = runTest {
        // Mock the use case response
        val response = arrayListOf(
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
        coEvery { databaseRepository.getQuizzes(any()) } returns flowOf(Resource.Success(response))

        // Call the function to test
        val result = getQuizzesUseCase("Any string")

        // Assert the correctness of the response
        assertEquals(response, result.first().data)
    }

    @Test
    fun `getRankingUseCase should return scores from database`() = runTest {
        // Mock the use case response
        val response = arrayListOf(
            Rank(index = 0, isMe = false, username = "User 1", image = "", maxScore = 100.0),
            Rank(index = 1, isMe = true, username = "User 2", image = "", maxScore = 90.0),
            Rank(index = 2, isMe = false, username = "User 3", image = "", maxScore = 80.0),
        )
        coEvery { databaseRepository.getRanking(any()) } returns flowOf(Resource.Success(response))

        // Call the function to test
        val result = getRankingUseCase("Any string")

        // Assert the correctness of the response
        assertEquals(response, result.first().data)
    }

    @Test
    fun `isToSaveUseCase should return scores from database`() = runTest {
        // Mock the use case response
        val response = true
        coEvery { databaseRepository.isToSave(any(), any()) } returns Resource.Success(response)

        // Call the function to test
        val result = isToSaveUseCase("Any string", "Any string").toList()

        // Assert that the first emitted is of type resource loading
        assertTrue(result[0] is Resource.Loading)

        // Assert the correctness of the response
        assertEquals(response, result[1].data)
    }

    @Test
    fun `updateEndlessUseCase should return scores from database`() = runTest {
        // Mock the use case response
        val response = true
        coEvery { databaseRepository.updateEndlessQuiz(any()) } returns Resource.Success(response)

        // Call the function to test
        val result = updateEndlessUseCase(EndlessQuiz())

        // Assert the correctness of the response
        assertEquals(response, result.data)
    }

    @Test
    fun `updateQuizUseCase should return scores from database`() = runTest {
        // Mock the use case response
        val response = true
        coEvery { databaseRepository.updateQuiz(any()) } returns Resource.Success(response)

        // Call the function to test
        val result = updateQuizUseCase(Quiz())

        // Assert the correctness of the response
        assertEquals(response, result.data)
    }
}
