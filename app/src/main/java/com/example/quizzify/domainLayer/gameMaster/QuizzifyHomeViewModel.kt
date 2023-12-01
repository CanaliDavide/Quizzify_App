package com.example.quizzify.domainLayer.gameMaster

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizzify.dataLayer.common.Resource
import com.example.quizzify.dataLayer.database.data.Quiz
import com.example.quizzify.di.IoDispatcher
import com.example.quizzify.domainLayer.dbUseCase.GetGameTypeUseCase
import com.example.quizzify.domainLayer.dbUseCase.GetQuizzesUseCase
import com.example.quizzify.ui.composable.GameType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QuizzifyHomeUIState(

    val _loading_quiz: Boolean = false,
    val _loading_completed: Boolean = false,

    val loading: Boolean = false,

    val quizzes_artist: ArrayList<Quiz> = arrayListOf(),
    val quizzes_playlist: ArrayList<Quiz> = arrayListOf(),
    val quizzes_album: ArrayList<Quiz> = arrayListOf(),

    val artist_quizType: ArrayList<GameType> = arrayListOf(),
    val playlist_quizType: ArrayList<GameType> = arrayListOf(),
    val album_quizType: ArrayList<GameType> = arrayListOf(),
    val online_quizType: GameType = GameType(),
    val endless_quizType: GameType = GameType(),

    val errorOccurred: Boolean = false,
    val errorMessage: String = "",

    val currentCategory: String = "ARTIST"
)

@HiltViewModel
class QuizzifyHomeViewModel @Inject constructor(
    private val getQuizzesUseCase: GetQuizzesUseCase,
    private val getGameTypeUseCase: GetGameTypeUseCase,
    @IoDispatcher private val dispatcher : CoroutineDispatcher
) : ViewModel() {

    private val _uiState = mutableStateOf(QuizzifyHomeUIState(loading = true, _loading_completed = true, _loading_quiz = true))
    val uiState: State<QuizzifyHomeUIState> = _uiState

    /**
     * Fetch all the different quizzes from the database
     */
    fun fetchQuizzes() {
        val thread: ArrayList<Job> = arrayListOf()
        viewModelScope.launch {
            thread.add(CoroutineScope(dispatcher).launch {
                getGameTypeUseCase("PLAYLIST").collect { res ->
                    when (res) {
                        is Resource.Success -> {
                            Log.d("Fetch", res.data!!.toString())
                            _uiState.value =
                                _uiState.value.copy(playlist_quizType = res.data as ArrayList<GameType>)
                        }
                        is Resource.Error -> {
                            _uiState.value = _uiState.value.copy(
                                errorOccurred = true,
                                errorMessage = res.message!!
                            )
                        }
                        is Resource.Loading -> {
                            Log.d("Fetch", "loading")
                        }
                    }
                }
            })
            thread.add(CoroutineScope(dispatcher).launch {
                getGameTypeUseCase("ARTIST").collect { res ->
                    when (res) {
                        is Resource.Success -> {
                            Log.d("Fetch", res.data!!.toString())
                            _uiState.value =
                                _uiState.value.copy(artist_quizType = res.data as ArrayList<GameType>)
                        }
                        is Resource.Error -> {
                            _uiState.value = _uiState.value.copy(
                                errorOccurred = true,
                                errorMessage = res.message!!
                            )
                        }
                        is Resource.Loading -> {
                            Log.d("Fetch", "loading")
                        }
                    }
                }
            })
            thread.add(CoroutineScope(dispatcher).launch {
                getGameTypeUseCase("ALBUM").collect { res ->
                    when (res) {
                        is Resource.Success -> {
                            Log.d("Fetch", res.data!!.toString())
                            _uiState.value =
                                _uiState.value.copy(album_quizType = res.data as ArrayList<GameType>)
                        }
                        is Resource.Error -> {
                            _uiState.value = _uiState.value.copy(
                                errorOccurred = true,
                                errorMessage = res.message!!
                            )
                        }
                        is Resource.Loading -> {
                            Log.d("Fetch", "loading")
                        }
                    }
                }
            })
            thread.add(CoroutineScope(dispatcher).launch {
                getGameTypeUseCase("ENDLESS").collect { res ->
                    when (res) {
                        is Resource.Success -> {
                            Log.d("Fetch", res.data!!.toString())
                            _uiState.value =
                                _uiState.value.copy(endless_quizType = res.data[0])
                        }
                        is Resource.Error -> {
                            _uiState.value = _uiState.value.copy(
                                errorOccurred = true,
                                errorMessage = res.message!!
                            )
                        }
                        is Resource.Loading -> {
                            Log.d("Fetch", "loading")
                        }
                    }
                }
            })
            thread.add(CoroutineScope(dispatcher).launch {
                getGameTypeUseCase("ONLINE").collect { res ->
                    when (res) {
                        is Resource.Success -> {
                            Log.d("Fetch", res.data!!.toString())
                            _uiState.value =
                                _uiState.value.copy(online_quizType = res.data[0])
                        }
                        is Resource.Error -> {
                            _uiState.value = _uiState.value.copy(
                                errorOccurred = true,
                                errorMessage = res.message!!
                            )
                        }
                        is Resource.Loading -> {
                            Log.d("Fetch", "loading")
                        }
                    }
                }
            })
            thread.forEach { it.join() }
            Log.d("Fetch", "completed first fetch")
            _uiState.value = _uiState.value.copy(_loading_quiz = false)
            _uiState.value = _uiState.value.copy(loading = _uiState.value._loading_completed || _uiState.value._loading_quiz)
        }

    }

    /**
     * Fetch all the completed quizzes from the database
     */
    fun fetchCompletedQuizzes() {
        val thread: ArrayList<Job> = arrayListOf()
        viewModelScope.launch {
            thread.add(CoroutineScope(dispatcher).launch {
                getQuizzesUseCase("ARTIST").collect { res ->
                    when (res) {
                        is Resource.Success -> {
                            _uiState.value =
                                _uiState.value.copy(quizzes_artist = res.data as ArrayList<Quiz>)
                        }
                        is Resource.Error -> {
                            _uiState.value = _uiState.value.copy(
                                errorOccurred = true,
                                errorMessage = res.message!!
                            )
                        }
                        is Resource.Loading -> {
                            Log.d("Fetch", "loading completed quizzes")
                        }
                    }
                }
            })
            thread.add(CoroutineScope(dispatcher).launch {
                getQuizzesUseCase("PLAYLIST").collect { res ->
                    when (res) {
                        is Resource.Success -> {
                            _uiState.value =
                                _uiState.value.copy(quizzes_playlist = res.data as ArrayList<Quiz>)

                        }
                        is Resource.Error -> {
                            _uiState.value = _uiState.value.copy(
                                errorOccurred = true,
                                errorMessage = res.message!!
                            )
                        }
                        is Resource.Loading -> {
                            Log.d("Fetch", "loading completed quizzes")
                        }
                    }
                }
            })
            thread.add(CoroutineScope(dispatcher).launch {
                getQuizzesUseCase("ALBUM").collect { res ->
                    when (res) {
                        is Resource.Success -> {
                            _uiState.value =
                                _uiState.value.copy(quizzes_album = res.data as ArrayList<Quiz>)
                        }
                        is Resource.Error -> {
                            _uiState.value = _uiState.value.copy(
                                errorOccurred = true,
                                errorMessage = res.message!!
                            )
                        }
                        is Resource.Loading -> {
                            Log.d("Fetch", "loading completed quizzes")
                        }
                    }
                }
            })
            thread.forEach { it.join() }
            _uiState.value = _uiState.value.copy(_loading_completed = false)
            _uiState.value = _uiState.value.copy(loading = _uiState.value._loading_completed || _uiState.value._loading_quiz)
        }
    }

    /**
     * Set the current page category
     */
    fun setCategory(cat: String) {
        _uiState.value = _uiState.value.copy(currentCategory = cat)
    }

    /**
     * Return is a specific game has been completed
     */
    fun isCompleted(tag: String): Boolean {
        return when (_uiState.value.currentCategory) {
            "ARTIST" -> _uiState.value.quizzes_artist.stream().anyMatch { it.id == tag }
            "PLAYLIST" -> _uiState.value.quizzes_playlist.stream()
                .anyMatch { it.id == tag }
            "ALBUM" -> _uiState.value.quizzes_album.stream().anyMatch { it.id == tag }
            else -> false
        }
    }
}