package com.example.quizzify.domainLayer.gameMaster

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizzify.dataLayer.common.Resource
import com.example.quizzify.dataLayer.database.data.CompetitiveResponse
import com.example.quizzify.dataLayer.database.data.Rank
import com.example.quizzify.domainLayer.dbUseCase.GetEndlessUseCase
import com.example.quizzify.domainLayer.dbUseCase.GetRankingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.streams.toList

data class RankGraphState(

    val _loading_graph: Boolean = false,
    val _loading_rank: Boolean = false,

    val loading: Boolean = false,
    val errorOccurred: Boolean = false,
    val errorMessage: String = "",

    val graph: CompetitiveResponse = CompetitiveResponse(
        id = "ENDLESS",
        collection = "ENDLESS",
        maxScore = 0.0,
        scores = arrayListOf()
    ),
    val ranking: ArrayList<Rank> = arrayListOf(),
    val myIndex: Int = 0
)

@HiltViewModel
class EndlessGraphViewModel @Inject constructor(
    private val getEndlessUseCase: GetEndlessUseCase,
    private val getRankingUseCase: GetRankingUseCase,
) : ViewModel() {

    private val _state = mutableStateOf(RankGraphState(loading = true, _loading_graph = true, _loading_rank = true))
    val state: State<RankGraphState> = _state


    fun fetchGraph() {
        viewModelScope.launch {
            getEndlessUseCase().collect { res ->
                when (res) {
                    is Resource.Success -> {
                        Log.d("RANKING", "Success")
                        _state.value = _state.value.copy(
                            graph = res.data!!
                        )
                    }
                    is Resource.Error -> {
                        _state.value = _state.value.copy(
                            errorOccurred = true,
                            errorMessage = res.message!!
                        )
                    }
                    is Resource.Loading -> {
                        Log.d("RANKING", "Loading")
                    }
                }
            }
            _state.value = _state.value.copy(_loading_graph = false)
            _state.value = _state.value.copy(loading = _state.value._loading_graph || _state.value._loading_rank)
        }
    }

    fun fetchRank() {
        viewModelScope.launch {
            getRankingUseCase("Endless").collect { res ->
                when (res) {
                    is Resource.Success -> {
                        try {

                            _state.value = _state.value.copy(
                                ranking = ArrayList(
                                    res.data!!.stream()
                                        .toList()
                                ),
                                myIndex = res.data.stream().filter { x -> x.isMe }!!
                                    .toList()[0].index
                            )
                        } catch (e: Exception) {
                            Log.d("RANKING", "Error")
                            _state.value = _state.value.copy(
                                errorOccurred = true,
                                errorMessage = "Error occurred while fetching ranking, please try again later."
                            )
                        }
                    }
                    is Resource.Error -> {
                        _state.value = _state.value.copy(
                            errorOccurred = true,
                            errorMessage = res.message!!
                        )
                    }
                    is Resource.Loading -> {
                        Log.d("RANKING", "Loading")
                    }
                }
            }
            _state.value = _state.value.copy(_loading_rank = false)
            _state.value = _state.value.copy(loading = _state.value._loading_graph || _state.value._loading_rank)
        }
    }


}