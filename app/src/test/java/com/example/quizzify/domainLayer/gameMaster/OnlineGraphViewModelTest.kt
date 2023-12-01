package com.example.quizzify.domainLayer.gameMaster

import com.example.quizzify.MainDispatcherRule
import com.example.quizzify.dataLayer.common.Resource
import com.example.quizzify.dataLayer.database.data.CompetitiveResponse
import com.example.quizzify.dataLayer.database.data.Rank
import com.example.quizzify.domainLayer.dbUseCase.GetOnlineUseCase
import com.example.quizzify.domainLayer.dbUseCase.GetRankingUseCase
import com.patrykandpatrick.vico.core.entry.FloatEntry
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class OnlineGraphViewModelTest{
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getOnlineUseCase: GetOnlineUseCase = mockk()
    private val getRankingUseCase: GetRankingUseCase = mockk()
    private val  viewModel = OnlineGraphViewModel(getOnlineUseCase, getRankingUseCase)

    @Before
    fun setUp() {
        clearAllMocks()
    }

    @Test
    fun `fetchGraph should update state with graph data on success`() = runTest {
        // Mock the use case response
        val graphData = CompetitiveResponse(
            id = "ONLINE",
            collection = "ONLINE",
            maxScore = 100.0,
            scores = arrayListOf(
                FloatEntry(1f, 100.0f), FloatEntry(2f, 90.0f), FloatEntry(3f, 80f)
            )
        )
        coEvery { getOnlineUseCase() } returns flowOf(Resource.Loading(), Resource.Success(graphData))

        // Call the function to test
        viewModel.fetchGraph()

        // Assert that the state is updated correctly
        val expectedState = OnlineGraphState(graph = graphData, ranking = ArrayList(), myIndex = 0)
        assertEquals(expectedState, viewModel.state.value)
    }

    @Test
    fun `fetchGraph should not update state with error on failure`() = runTest {
        // Mock the use case response
        val error = Resource.Error<CompetitiveResponse>("Error")
        coEvery { getOnlineUseCase() } returns flowOf(error)

        // Call the function to test
        viewModel.fetchGraph()

        // Assert that the state is updated correctly
        val expectedState = OnlineGraphState(_loading_graph=false, _loading_rank=false, loading=false, errorOccurred=true, errorMessage="Error")
        assertEquals(expectedState, viewModel.state.value)
    }

    @Test
    fun `fetchRank should update state with ranking data on success`() = runTest {
        // Mock the use case response
        val rankingData = arrayListOf(
            Rank(index = 0, isMe = false, username = "User 1", image = "", maxScore = 100.0),
            Rank(index = 1, isMe = true, username = "User 2", image = "", maxScore = 90.0),
            Rank(index = 2, isMe = false, username = "User 3", image = "", maxScore = 80.0),
        )
        coEvery { getRankingUseCase("Online") } returns flowOf(Resource.Loading(), Resource.Success(rankingData))

        // Call the function to test
        viewModel.fetchRank()

        // Assert that the state is updated correctly, with myIndex set to the index of the user
        // with isMe = true
        val expectedState = OnlineGraphState(ranking = ArrayList(rankingData), myIndex = 1)
        assertEquals(expectedState, viewModel.state.value)
    }

    @Test
    fun `fetchRank should not update state with error on failure`() = runTest {
        // Mock the use case response
        val error = Resource.Error<ArrayList<Rank>>("Error")
        coEvery { getRankingUseCase("Online") } returns flowOf(error)

        // Call the function to test
        viewModel.fetchRank()

        // Assert that the state is updated correctly
        val expectedState = OnlineGraphState(_loading_graph=false, _loading_rank=false, loading=false, errorOccurred=true, errorMessage="Error")
        assertEquals(expectedState, viewModel.state.value)
    }
}