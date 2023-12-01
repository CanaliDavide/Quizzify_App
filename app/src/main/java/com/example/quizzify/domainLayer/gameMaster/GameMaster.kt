package com.example.quizzify.domainLayer.gameMaster

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizzify.dataLayer.authenticator.UserData
import com.example.quizzify.dataLayer.common.Resource
import com.example.quizzify.dataLayer.common.SpotifyApiConst
import com.example.quizzify.dataLayer.database.data.EndlessQuiz
import com.example.quizzify.dataLayer.database.data.OnlineQuiz
import com.example.quizzify.dataLayer.database.data.Quiz
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
import com.example.quizzify.domainLayer.useCase.*
import com.example.quizzify.domainLayer.utils.QuizTimer
import com.example.quizzify.server.message.GameQuestion
import com.example.quizzify.ui.composable.GameType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket
import java.util.*
import javax.inject.Inject
import kotlin.random.Random
import kotlin.streams.toList
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

enum class ArtistQuestions {
    IMAGE, DESCRIPTION, GENRE, ALBUM_NUMBER
}

enum class AlbumQuestions {
    NUMBER_TRACKS, YEAR, SONG_DESC, LYRIC_MXM, REFERENT, PREVIEW, LYRIC_GENIUS, IMAGE
}

enum class PlaylistQuestions {
    SONG_DESC, LYRIC_MXM, REFERENT, PREVIEW, LYRIC_GENIUS, IMAGE, DESCRIPTION, GENRE
}


data class Question(
    val question: String? = null,
    val imageURL: String? = null,
    val songURL: String? = null,
    val answers: ArrayList<String>,

    val right: Int,
    val correctAnswerGiven: Boolean = false,
    val answerGiven: Boolean = false,

    val timeToAnswer: Duration
)

fun Question.toSerializable(): SerializableQuestion {
    return SerializableQuestion(
        question = question,
        imageURL = imageURL,
        songURL = songURL,
        answers = answers,
        right = right,
        timeToAnswer = timeToAnswer.toIsoString()
    )
}

@Serializable
data class SerializableQuestion(
    val question: String? = null,
    val imageURL: String? = null,
    val songURL: String? = null,
    val answers: ArrayList<String>,
    val right: Int,
    val timeToAnswer: String
)

fun SerializableQuestion.toQuestion(): Question {
    return Question(
        question = question,
        imageURL = imageURL,
        songURL = songURL,
        answers = answers,
        right = right,
        timeToAnswer = Duration.parseIsoString(timeToAnswer)
    )
}

@Serializable
data class OnlineHistory(
    val otherPlayerCurrent: Int = 0,
    val otherPlayerHistory: ArrayList<Boolean> = arrayListOf(),
)

data class ConnectionState(
    val isLoading: Boolean = true,
    val reader: BufferedReader? = null,
    val writer: BufferedWriter? = null
)

data class TimerState(
    val time: Float = 0.0f,
    val active: Boolean = false,
)

data class GameState(

    val error_occurred: Boolean = false,
    val error_message: String = "",

    val gameType: GameType = GameType(),

    val score: Int = 0,
    val currentQuizId: Int = 0,
    val isAnswered: Boolean = false,
    val isFailed: Boolean = false,
    val isToSave: Boolean = true,
    val questions: ArrayList<Question> = arrayListOf(),

    val onlineHistory: OnlineHistory = OnlineHistory(),

    val readyToStart: Boolean = false,
    val rotationCard: Float = 0f,

    val loadingText: String = "Loading...",

    val canShowEndGame: Boolean = false,
    val didIWin: Boolean = false,
)

fun GameState.toSerializable(): GameQuestion {
    return GameQuestion(
        gameType = gameType,
        questions = ArrayList(questions.map { it.toSerializable() })
    )
}

@HiltViewModel
class GameMaster @Inject constructor(
    private val getTopArtistsUseCase: GetTopArtistsUseCase,
    private val getTopSongsUseCase: GetTopSongsUseCase,
    private val descriptionArtistQuestion: DescriptionArtistQuestion,
    private val numberAlbumArtistQuestion: NumberAlbumArtistQuestion,
    private val getUserAlbumsUseCase: GetUserAlbumsUseCase,
    private val descriptionSongQuestion: DescriptionSongQuestion,
    private val lyricsSongMXMQuestion: LyricsSongMXMQuestion,
    private val getRecommendationsArtistsUseCase: GetRecommendationsArtistsUseCase,
    private val getRecommendationsAlbumsUseCase: GetRecommendationsAlbumsUseCase,
    private val getRecommendationsTracksUseCase: GetRecommendationsTracksUseCase,
    private val updateQuizUseCase: UpdateQuizUseCase,
    private val updateEndlessUseCase: UpdateEndlessUseCase,
    private val isToSaveUseCase: IsToSaveUseCase,
    private val getArtistFromPlaylistUseCase: GetArtistFromPlaylistUseCase,
    private val getPlaylistUseCase: GetPlaylistUseCase,
    private val getAlbumFromPlaylistUseCase: GetAlbumFromPlaylistUseCase,
    private val getAlbumFromArtistsUseCase: GetAlbumFromArtistsUseCase,
    private val updateOnlineUseCase: UpdateOnlineUseCase,
) : ViewModel() {

    private var clientSocket: Socket? = null
    private var writer: BufferedWriter? = null
    private var isFetching: Boolean = false
    private val userArtists: ArrayList<Artist> = arrayListOf()
    private val userAlbums: ArrayList<Album> = arrayListOf()
    private val userSongs: ArrayList<Track> = arrayListOf()

    private var hasMoreArtists: Boolean = true
    private var hasMoreSongs: Boolean = true
    private var hasMoreAlbums: Boolean = true

    private var artistOffset = 5
    private var albumsOffset = 5
    private var playlistOffset = 5

    private var artistsList: List<Int> = listOf()
    private var albumsList: List<Int> = listOf()
    private var playlistsList: List<Int> = listOf()

    private val _stateGame = mutableStateOf(GameState())
    val stateGame: State<GameState> = _stateGame

    private val _stateTimer = mutableStateOf(TimerState())
    val stateTimer: State<TimerState> = _stateTimer

    private val _stateConnection = mutableStateOf(ConnectionState())
    val stateConnection: State<ConnectionState> = _stateConnection

    private var timerJob: Job? = null

    /**
     * @return True if it's the last question of the quiz
     */
    fun isLastQuestion(): Boolean {
        return _stateGame.value.currentQuizId == _stateGame.value.questions.size - 1
    }

    /**
     * Stop the clock.
     */
    private fun stopTimer() {
        _stateTimer.value = _stateTimer.value.copy(active = false)

        if (timerJob != null) {
            timerJob!!.cancel()
        }
    }

    /**
     * Reset the clock.
     */
    fun resetTimer() {
        QuizTimer.reset()
        _stateTimer.value = _stateTimer.value.copy(time = 0.0f, active = false)
    }


    /**
     * Start the clock for the question.
     */
    fun startTimer(duration: Duration, period: Duration = 10.milliseconds) {
        if (!_stateTimer.value.active) {
            resetTimer()
            _stateTimer.value = _stateTimer.value.copy(active = true)
            QuizTimer.duration = duration
            QuizTimer.period = period

            timerJob = viewModelScope.launch {
                QuizTimer.tick.collect { time ->
                    _stateTimer.value = _stateTimer.value.copy(time = time)
                    if (time == 1.0f) {
                        val modQuestions = _stateGame.value.questions.toMutableList()
                        val idCurr = _stateGame.value.currentQuizId
                        val id = (0..4).filter { it != modQuestions[idCurr].right }.random()
                        answerClicked(id)
                    }
                }
            }
        }
    }

    /**
     * Set the game type in the state
     */
    fun setGameType(gType: GameType) {
        _stateGame.value = _stateGame.value.copy(gameType = gType)
    }


    /**
     * Set the quiz state to the next question.
     */
    fun nextQuiz() {
        _stateGame.value = _stateGame.value.copy(isAnswered = false)

        if (_stateGame.value.gameType.category == "ENDLESS") {
            val questions = _stateGame.value.questions.drop(1)
            _stateGame.value = _stateGame.value.copy(
                questions = ArrayList(questions)
            )
            if (_stateGame.value.currentQuizId > (_stateGame.value.questions.size - 5) && !isFetching) {
                viewModelScope.launch {
                    initUnlimitedQuestions()
                }
            }
        } else {
            _stateGame.value =
                _stateGame.value.copy(currentQuizId = _stateGame.value.currentQuizId + 1)
        }
    }

    /**
     * Online - Other player history of wrong and right responses
     */
    fun currentHistory(): List<Boolean> {
        return _stateGame.value.questions.map { it.correctAnswerGiven }
    }

    /**
     * Called when the user press an answer card
     *
     * @param id: the id of the card pressed.
     */
    fun answerClicked(id: Int) {
        stopTimer()
        rotateCard()
        val modQuestions = _stateGame.value.questions.toMutableList()
        val idCurr = _stateGame.value.currentQuizId
        if (id == modQuestions[idCurr].right) {
            modQuestions[idCurr] = modQuestions[idCurr].copy(correctAnswerGiven = true)
            _stateGame.value = _stateGame.value.copy(
                questions = ArrayList(modQuestions)
            )
            _stateGame.value = _stateGame.value.copy(
                score = _stateGame.value.score + 1
            )
        } else {
            _stateGame.value = _stateGame.value.copy(isFailed = true)
        }

        if (_stateGame.value.gameType.category == "ONLINE") {
            sendUpdate()
            when (isWinner()) {
                0 -> {
                    sendEnd(true)
                }
                1 -> {
                    sendEnd(false)
                }
                2 -> {}
            }
        }

        _stateGame.value = _stateGame.value.copy(isAnswered = true)
    }


    /**
     * Create questions based on the chosen quiz.
     */
    fun fetchQuestions(): Job {
        return viewModelScope.launch {
            try {
                _stateConnection.value = _stateConnection.value.copy(isLoading = false)
                setToSave()
                _stateGame.value =
                    _stateGame.value.copy(loadingText = _stateGame.value.gameType.loading)
                when (_stateGame.value.gameType.category) {
                    "ONLINE" -> {
                        startClient()
                    }
                    "ENDLESS" -> {
                        startGameUnlimited()
                    }
                    "ARTIST" -> {
                        when (_stateGame.value.gameType.keyword) {
                            "MYTOPARTIST" -> {
                                startGameArtistUser()
                            }
                            else -> {
                                startGameArtist(_stateGame.value.gameType.idSpotify)
                            }
                        }
                    }
                    "ALBUM" -> {
                        when (_stateGame.value.gameType.keyword) {
                            "MYTOPALBUM" -> {
                                startGameAlbumUser()
                            }
                            "ALBUMFROMPARTIST" -> {
                                val seeds = _stateGame.value.gameType.idSpotify.split(",")
                                startGameAlbum(artistIds = seeds)
                            }
                            else -> {
                                startGameAlbum(_stateGame.value.gameType.idSpotify)
                            }
                        }
                    }
                    "PLAYLIST" -> {
                        when (_stateGame.value.gameType.keyword) {
                            "MYTOPPLAYLIST" -> {
                                startGamePlaylistUser()
                            }
                            else -> {
                                startGamePlaylist(_stateGame.value.gameType.idSpotify)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                _stateGame.value = _stateGame.value.copy(
                    loadingText = "Error while loading the game"
                )
                _stateGame.value = _stateGame.value.copy(
                    error_occurred = true
                )
            }
        }
    }

    /**
     * Starts the game for the "Album" category.
     *
     * @param playlistId The ID of the playlist to retrieve albums from. (Optional)
     * @param artistIds The list of artist IDs to retrieve albums from. (Optional)
     */
    private suspend fun startGameAlbum(
        playlistId: String = "",
        artistIds: List<String> = listOf()
    ) {
        if (playlistId.isNotBlank()) {
            getAlbumFromPlayList(playlistId)
        } else {
            getAlbumFromArtists(artistIds)
        }
        initAlbumQuestions()
    }

    /**
     * Retrieves albums from the given artist IDs and adds them to the user's album collection.
     *
     * @param artistIds The list of artist IDs to retrieve albums from.
     */
    private suspend fun getAlbumFromArtists(artistIds: List<String>) {
        getAlbumFromArtistsUseCase(artistIds).collect { res ->
            when (res) {
                is Resource.Success -> {
                    userAlbums.addAll(res.data!!)
                }
                is Resource.Error -> {
                    _stateGame.value = _stateGame.value.copy(error_occurred = true)
                    _stateGame.value = _stateGame.value.copy(
                        error_message = res.message!!
                    )
                }
                is Resource.Loading -> {
                    Log.d("GameMaster", "Loading getAlbumFromArtists")
                }
            }
        }
    }

    /**
     * Retrieves albums from the given playlist ID and adds them to the user's album collection.
     *
     * @param playlistId The ID of the playlist to retrieve albums from.
     */
    private suspend fun getAlbumFromPlayList(playlistId: String) {
        getAlbumFromPlaylistUseCase(playlistId).collect { res ->
            when (res) {
                is Resource.Success -> {
                    userAlbums.addAll(res.data!!)
                }
                is Resource.Error -> {
                    _stateGame.value = _stateGame.value.copy(error_occurred = true)
                    _stateGame.value = _stateGame.value.copy(
                        error_message = res.message!!
                    )
                }
                is Resource.Loading -> {
                    Log.d("GameMaster", "Loading getAlbumFromPlayList")
                }
            }
        }
    }

    /**
     * Starts the game for the "Playlist" category with the specified playlist ID.
     *
     * @param playlistId The ID of the playlist to play the game with.
     */
    private suspend fun startGamePlaylist(playlistId: String) {
        getPlaylist(playlistId)
        initPlaylistQuestions()
    }

    /**
     * Starts the game for the "Playlist" category with the specified playlist ID.
     *
     * @param playlistId The ID of the playlist to play the game with.
     */
    private suspend fun getPlaylist(playlistId: String) {
        getPlaylistUseCase(playlistId).collect { res ->
            when (res) {
                is Resource.Success -> {
                    userSongs.addAll(res.data!!)
                }
                is Resource.Error -> {
                    _stateGame.value = _stateGame.value.copy(error_occurred = true)
                    _stateGame.value = _stateGame.value.copy(
                        error_message = res.message!!
                    )
                }
                is Resource.Loading -> {
                    Log.d("GameMaster", "Loading getPlaylist")
                }
            }
        }
    }

    /**
     * Starts the game for the "Artist" category with the specified playlist ID.
     *
     * @param playlistId The ID of the playlist to retrieve artists from.
     */
    private suspend fun startGameArtist(playlistId: String) {
        getArtistFromPlaylist(playlistId)
        initArtistQuestions()
    }

    /**
     * Retrieves artists from the given playlist ID and adds them to the user's artist collection.
     *
     * @param playlistId The ID of the playlist to retrieve artists from.
     */
    private suspend fun getArtistFromPlaylist(playlistId: String) {
        getArtistFromPlaylistUseCase(playlistId).collect { res ->
            when (res) {
                is Resource.Success -> {
                    userArtists.addAll(res.data!!)
                }
                is Resource.Error -> {
                    _stateGame.value = _stateGame.value.copy(error_occurred = true)
                    _stateGame.value = _stateGame.value.copy(
                        error_message = res.message!!
                    )
                }
                is Resource.Loading -> {
                    Log.d("GameMaster", "Loading getArtistFromPlaylist")
                }
            }
        }
    }

    /**
     * Starts the game in the "Unlimited" category.
     * Retrieves the user's playlist, albums, and artists and initializes the unlimited game questions.
     */
    private suspend fun startGameUnlimited() {
        getUserPlaylist()
        getUserAlbums()
        getUserArtists()
        initUnlimitedQuestions()
    }

    /**
     * Starts the game in the "Playlist User" category.
     * Retrieves the user's playlist and initializes the playlist game questions.
     */
    private suspend fun startGamePlaylistUser() {
        getUserPlaylist()
        initPlaylistQuestions()
    }

    /**
     * Starts the game in the "Album User" category.
     * Retrieves the user's albums and initializes the album game questions.
     */
    private suspend fun startGameAlbumUser() {
        getUserAlbums()
        initAlbumQuestions()
    }

    /**
     * Start the quiz based on the user's top artists.
     */
    private suspend fun startGameArtistUser() {
        getUserArtists()
        initArtistQuestions()
    }

    /**
     * Sets the save state of the game.
     * Retrieves the save state from the "isToSave" use case based on the current game type's keyword and category.
     */
    private suspend fun setToSave() {
        isToSaveUseCase(
            stateGame.value.gameType.keyword,
            _stateGame.value.gameType.category
        ).collect { res ->
            when (res) {
                is Resource.Success -> _stateGame.value =
                    _stateGame.value.copy(isToSave = res.data!!)
                is Resource.Error -> {
                    _stateGame.value = _stateGame.value.copy(error_occurred = true)
                    _stateGame.value = _stateGame.value.copy(
                        error_message = res.message!!
                    )
                }
                is Resource.Loading -> {
                    Log.d("GameMaster", "Loading setToSave")
                }
            }
        }
    }


    /**
     * Get the user's top artists from Spotify.
     *
     * @param limit: max number of artists.
     * @param offset: offset from the start of the top artist array fetched from spotify.
     */
    private suspend fun getUserArtists(limit: Int = 5, offset: Int = 0) {
        if (hasMoreArtists) {
            getTopArtistsUseCase(limit = limit, offset = offset).collect { res ->
                when (res) {
                    is Resource.Success -> {
                        userArtists.addAll(res.data!!.artists) // add artists found
                        if (userArtists.size < limit) {
                            hasMoreArtists = false
                            getUserArtistsRec(limit - userArtists.size)
                        } else {
                            if (UserData.seed_artists.isEmpty()) {
                                userArtists.stream().map { it.id }
                                    .forEach { UserData.seed_artists.add(it) }
                            }
                        }
                    }
                    is Resource.Error -> {
                        hasMoreArtists = false
                    }
                    is Resource.Loading -> {
                        Log.d("GameMaster", "Loading getUserArtists")
                    }
                }
            }
        } else {
            getUserArtistsRec(limit)
        }
    }

    /**
     * Retrieves recommended artists for the user based on their seed artists.
     * Calls the "getRecommendationsArtistsUseCase" use case to fetch the recommendations.
     * Adds the recommended artists to the userArtists list.
     * @param limit The maximum number of recommended artists to retrieve.
     */
    private suspend fun getUserArtistsRec(limit: Int = 5) {
        var seed = SpotifyApiConst.seed_artists
        if (UserData.seed_artists.isNotEmpty()) {
            seed = ArrayList(UserData.seed_artists.take(5))
        }
        var finalSeed = ""
        for (s in seed) {
            finalSeed += ",$s"
        }
        finalSeed = finalSeed.drop(1)
        getRecommendationsArtistsUseCase(finalSeed, "", "", limit).collect { res ->
            when (res) {
                is Resource.Success -> {
                    userArtists.addAll(res.data!!)
                }
                is Resource.Error -> {
                    _stateGame.value = _stateGame.value.copy(error_occurred = true)
                    _stateGame.value = _stateGame.value.copy(
                        error_message = res.message!!
                    )
                }
                is Resource.Loading -> {
                    Log.d("GameMaster", "Loading getUserArtistsRec")
                }
            }
        }
    }

    /**
     * Initializes the artist-related questions for the game.
     * Creates questions based on the available userArtists and the types of artist questions.
     * Updates the stateGame value to indicate that the game is ready to start.
     */
    private suspend fun initArtistQuestions() {
        val typesOfQuestions = ArtistQuestions.values().size

        artistsList = (1..(userArtists.size * typesOfQuestions)).shuffled()
        while (stateGame.value.questions.size < stateGame.value.gameType.numberOfQuestions && artistsList.isNotEmpty()) {
            val questionType: Int = artistsList.take(1)[0]
            artistsList = artistsList.drop(1)

            createArtistsQuestions(
                questionType = (questionType - 1) % typesOfQuestions,
                artistIndex = (questionType - 1).floorDiv(typesOfQuestions)
            )
        }

        _stateGame.value = _stateGame.value.copy(readyToStart = true)
    }

    /**
     * Retrieves user albums and adds them to the userAlbums list.
     * Calls the "getUserAlbumsUseCase" use case to fetch the user albums.
     * If there are more albums to fetch, recursively calls "getUserAlbumsRec" to fetch additional albums.
     * Updates the UserData seed tracks and seed artists based on the retrieved albums.
     * @param limit The maximum number of albums to retrieve.
     * @param offset The offset for pagination when retrieving albums.
     */
    private suspend fun getUserAlbums(limit: Int = 5, offset: Int = 0) {
        if (hasMoreAlbums) {
            getUserAlbumsUseCase(limit = limit, offset = offset).collect { res ->
                when (res) {
                    is Resource.Success -> {
                        userAlbums.addAll(res.data!!.albums)
                        if (userAlbums.size < limit) {
                            hasMoreAlbums = false
                            getUserAlbumsRec(limit - userAlbums.size)
                        } else {
                            if (UserData.seed_tracks.isEmpty()) {
                                userAlbums.stream().map { it.tracks[0].id }
                                    .forEach { UserData.seed_tracks.add(it) }
                            }
                            if (UserData.seed_artists.isEmpty()) {
                                userAlbums.stream().map { it.tracks[0].artists[0].id }
                                    .forEach { UserData.seed_artists.add(it) }
                            }
                        }
                    }
                    is Resource.Error -> {
                        hasMoreAlbums = false
                    }
                    is Resource.Loading -> {
                        Log.d("GameMaster", "Loading getUserAlbums")
                    }
                }
            }
        } else {
            getUserAlbumsRec(limit)
        }
    }

    /**
     * Retrieves recommended albums for the user based on their seed artists and seed tracks.
     * Calls the "getRecommendationsAlbumsUseCase" use case to fetch the recommendations.
     * Adds the recommended albums to the userAlbums list.
     * @param limit The maximum number of recommended albums to retrieve.
     */
    private suspend fun getUserAlbumsRec(limit: Int = -1) {
        var seedArtists = SpotifyApiConst.seed_artists
        var seedTracks = SpotifyApiConst.seed_tracks

        if (UserData.seed_artists.isNotEmpty()) {
            seedArtists = UserData.seed_artists
        }
        if (UserData.seed_tracks.isNotEmpty()) {
            seedTracks = UserData.seed_tracks
        }

        val n = Random.nextInt(1, 5)
        val seedA = seedArtists.take(n)
        val seedT = seedTracks.take(5 - n)

        var finalSeedArtist = ""
        for (s in seedA) {
            finalSeedArtist += ",$s"
        }
        finalSeedArtist = finalSeedArtist.drop(1)

        var finalSeedTrack = ""
        for (s in seedT) {
            finalSeedTrack += ",$s"
        }
        finalSeedTrack = finalSeedTrack.drop(1)

        getRecommendationsAlbumsUseCase(finalSeedArtist, "", finalSeedTrack, limit).collect { res ->
            when (res) {
                is Resource.Success -> {
                    userAlbums.addAll(res.data!!)
                }
                is Resource.Error -> {
                    _stateGame.value = _stateGame.value.copy(error_occurred = true)
                    _stateGame.value = _stateGame.value.copy(
                        error_message = res.message!!
                    )
                }
                is Resource.Loading -> {
                    Log.d("GameMaster", "Loading getUserAlbumsRec")
                }
            }
        }
    }

    /**
     * Initializes the album-related questions for the game.
     * Creates questions based on the available userAlbums and the types of album questions.
     * Updates the stateGame value to indicate that the game is ready to start.
     */
    private suspend fun initAlbumQuestions() {
        val typesOfQuestions = AlbumQuestions.values().size

        albumsList = (1..(userAlbums.size * typesOfQuestions)).shuffled()
        while (stateGame.value.questions.size < stateGame.value.gameType.numberOfQuestions && albumsList.isNotEmpty()) {
            val questionType: Int = albumsList.take(1)[0]
            albumsList = albumsList.drop(1)

            createAlbumQuestions(
                questionType = (questionType - 1) % typesOfQuestions,
                albumIndex = (questionType - 1).floorDiv(typesOfQuestions)
            )
        }
        _stateGame.value = _stateGame.value.copy(readyToStart = true)
    }

    /**
     * Fetches user playlists.
     *
     * @param limit The maximum number of playlists to fetch. Default is 5.
     * @param offset The offset value for pagination. Default is 0.
     */
    private suspend fun getUserPlaylist(limit: Int = 5, offset: Int = 0) {
        if (hasMoreSongs) {
            getTopSongsUseCase(limit = limit, offset = offset).collect { res ->
                when (res) {
                    is Resource.Success -> {
                        userSongs.addAll(res.data!!.tracks) // add songs found
                        if (userSongs.size < limit) {
                            hasMoreSongs = false
                            getUserTracksRec(limit - userSongs.size)
                        } else {
                            if (UserData.seed_tracks.isEmpty()) {
                                userSongs.stream().map { it.id }
                                    .forEach { UserData.seed_tracks.add(it) }
                            }
                        }
                    }
                    is Resource.Error -> {
                        hasMoreSongs = false
                    }
                    is Resource.Loading -> {
                        Log.d("GameMaster", "Loading getUserPlaylist")
                    }
                }
            }
        } else {
            getUserTracksRec(limit)
        }
    }

    /**
     * Fetches additional user tracks for the playlist.
     *
     * @param limit The maximum number of tracks to fetch. Default is -1, which means no limit.
     */
    private suspend fun getUserTracksRec(limit: Int = -1) {
        var seed = SpotifyApiConst.seed_tracks
        if (UserData.seed_tracks.isNotEmpty()) {
            seed = ArrayList(UserData.seed_tracks.take(5))
        }
        var finalSeed = ""
        for (s in seed) {
            finalSeed += ",$s"
        }
        finalSeed = finalSeed.drop(1)
        getRecommendationsTracksUseCase("", "", finalSeed, limit).collect { res ->
            when (res) {
                is Resource.Success -> {
                    userSongs.addAll(res.data!!)
                }
                is Resource.Error -> {
                    _stateGame.value = _stateGame.value.copy(error_occurred = true)
                    _stateGame.value = _stateGame.value.copy(
                        error_message = res.message!!
                    )
                }
                is Resource.Loading -> {
                    Log.d("GameMaster", "Loading getUserTracksRec")
                }
            }
        }
    }

    /**
     * Initializes the playlist-related questions for the game.
     */
    private suspend fun initPlaylistQuestions() {
        val typesOfQuestions = PlaylistQuestions.values().size

        playlistsList = (1..(userSongs.size * typesOfQuestions)).shuffled()
        while (stateGame.value.questions.size < stateGame.value.gameType.numberOfQuestions && playlistsList.isNotEmpty()) {
            val questionType: Int = playlistsList.take(1)[0]
            playlistsList = playlistsList.drop(1)

            createPlaylistQuestions(
                questionType = (questionType - 1) % typesOfQuestions,
                songIndex = (questionType - 1).floorDiv(typesOfQuestions)
            )
        }
        _stateGame.value = _stateGame.value.copy(readyToStart = true)
    }

    /**
     * Initializes the unlimited mode questions for the game.
     */
    private suspend fun initUnlimitedQuestions(){
        isFetching = true
        val typesOfArtists = ArtistQuestions.values().size
        val typesOfAlbums = AlbumQuestions.values().size
        val typesOfPlaylists = PlaylistQuestions.values().size

        artistsList = (1..(userArtists.size * typesOfArtists)).shuffled()
        albumsList = (1..(userAlbums.size * typesOfAlbums)).shuffled()
        playlistsList = (1..(userSongs.size * typesOfPlaylists)).shuffled()

        if (artistsList.size < 5) {
            userArtists.clear()
            getUserArtists(offset = artistOffset)
            artistsList = (1..(userArtists.size * typesOfArtists)).shuffled()
            artistOffset += 5
        }
        if (albumsList.size < 5) {
            userAlbums.clear()
            getUserAlbums(offset = albumsOffset)
            albumsList = (1..(userAlbums.size * typesOfAlbums)).shuffled()
            albumsOffset += 5
        }
        if (playlistsList.size < 5) {
            userSongs.clear()
            getUserPlaylist(offset = playlistOffset)
            playlistsList = (1..(userSongs.size * typesOfPlaylists)).shuffled()
            playlistOffset += 5
        }


        while ((artistsList.size + albumsList.size + playlistsList.size) > 20) {
            val possibility = arrayListOf(0, 1, 2)
            if (artistsList.isEmpty())
                possibility.remove(0)
            if (albumsList.isEmpty())
                possibility.remove(1)
            if (playlistsList.isEmpty())
                possibility.remove(2)

            if (possibility.isEmpty()) {
                _stateGame.value = _stateGame.value.copy(error_occurred = true)
                _stateGame.value = _stateGame.value.copy(
                    error_message = "Not enough data to play the game."
                )
            }

            if (_stateGame.value.questions.size >= 15) {
                _stateGame.value = _stateGame.value.copy(readyToStart = true)
            }

            when (possibility[Random.nextInt(possibility.size)]) {
                0 -> {
                    val questionType: Int = artistsList.take(1)[0]
                    artistsList = artistsList.drop(1)

                    createArtistsQuestions(
                        questionType = (questionType - 1) % typesOfArtists,
                        artistIndex = (questionType - 1).floorDiv(typesOfArtists)
                    )
                }
                1 -> {
                    val questionType: Int = albumsList.take(1)[0]
                    albumsList = albumsList.drop(1)

                    createAlbumQuestions(
                        questionType = (questionType - 1) % typesOfAlbums,
                        albumIndex = (questionType - 1).floorDiv(typesOfAlbums)
                    )
                }
                2 -> {
                    val questionType: Int = playlistsList.take(1)[0]
                    playlistsList = playlistsList.drop(1)

                    createPlaylistQuestions(
                        questionType = (questionType - 1) % typesOfPlaylists,
                        songIndex = (questionType - 1).floorDiv(typesOfPlaylists)
                    )
                }
                else -> {
                    _stateGame.value = _stateGame.value.copy(error_occurred = true)
                    _stateGame.value = _stateGame.value.copy(
                        error_message = "Unexpected error occurred."
                    )
                }
            }
        }
        isFetching = false
    }

    /**
     * Creates playlist-related questions based on the given question type and song index.
     *
     * @param questionType The type of playlist question.
     * @param songIndex The index of the song to create the question for.
     */
    private suspend fun createPlaylistQuestions(questionType: Int, songIndex: Int) {
        val song = userSongs[songIndex]

        when (questionType) {
            PlaylistQuestions.SONG_DESC.ordinal -> {
                if (userSongs.size >= 4) {
                    val artist = song.artists[0].name
                    var myRange = (0 until userSongs.size).toList().shuffled()
                    myRange = myRange.minus(songIndex)
                    val answers: ArrayList<String> = arrayListOf()

                    answers.addAll(
                        myRange.take(3)
                            .stream()
                            .map { userSongs[it].title }
                            .toList())

                    createSongDescriptionQuestion(artist, song.title, answers)

                }
            }
            PlaylistQuestions.LYRIC_MXM.ordinal -> {
                if (userSongs.size >= 4) {
                    val artist = song.artists[0].name
                    var myRange = (0 until userSongs.size).toList().shuffled()
                    myRange = myRange.minus(songIndex)
                    val answers: ArrayList<String> = arrayListOf()

                    answers.addAll(
                        myRange.take(3)
                            .stream()
                            .map { userSongs[it].title }
                            .toList())

                    createLyricMxmQuestion(artist, song.title, answers)

                }
            }
            PlaylistQuestions.REFERENT.ordinal -> {
                // case for REFERENT
                println("REFERENT selected")
            }
            PlaylistQuestions.PREVIEW.ordinal -> {
                /*
                if (userSongs.size >= 4 && !song.preview_url.isNullOrBlank()) {
                    var myRange = (0 until userSongs.size).toList().shuffled()
                    myRange = myRange.minus(songIndex)
                    val answers: ArrayList<String> = arrayListOf()

                    val previewUrl = song.preview_url

                    answers.addAll(
                        myRange.take(3)
                            .stream()
                            .map { userSongs[it].title }
                            .toList())

                    createSongPreviewFromAlbumQuestion(
                        "your playlist",
                        song.title,
                        previewUrl,
                        answers
                    )
                }
                */
            }
            PlaylistQuestions.LYRIC_GENIUS.ordinal -> {
                // case for LYRIC_GENIUS
                println("LYRIC_GENIUS selected")
            }
            PlaylistQuestions.IMAGE.ordinal -> {
                val artist = song.artists[0]
                if (artist.images.isNotEmpty()) {
                    val answers = ArrayList(userSongs.stream()
                        .map { it.artists[0].name }
                        .filter { it != artist.name }
                        .toList().shuffled().take(3))

                    createArtistImageQuestion(artist.images[0], artist.name, answers)
                }
            }
            PlaylistQuestions.DESCRIPTION.ordinal -> {
                val answers = ArrayList(userSongs.stream()
                    .map { it.artists[0].name }
                    .filter { it != song.artists[0].name }
                    .toList().shuffled().take(3))
                createArtistDescriptionQuestion(song.artists[0].name, answers)
            }
            PlaylistQuestions.GENRE.ordinal -> {
                if (song.artists[0].genres.isNotEmpty())
                    createArtistGenresQuestion(
                        song.artists[0].name,
                        ArrayList(song.artists[0].genres)
                    )
            }
            else -> {
                _stateGame.value = _stateGame.value.copy(error_occurred = true)
                _stateGame.value = _stateGame.value.copy(
                    error_message = "Unexpected error occurred."
                )
            }
        }

    }

    /**
     * Based on question type and artist index create a question.
     *
     * @param questionType: the type of question to create.
     * @param albumIndex: the index of the album in the userAlbum array.
     */
    private suspend fun createAlbumQuestions(questionType: Int, albumIndex: Int) {
        val album = userAlbums[albumIndex]

        when (questionType) {
            AlbumQuestions.LYRIC_GENIUS.ordinal -> {
                //TODO
            }

            AlbumQuestions.LYRIC_MXM.ordinal -> {
                if (album.totalTracks >= 4) {
                    var myRange = (0 until album.tracks.size).toList().shuffled()

                    val songIndex = myRange.take(1)[0]
                    myRange = myRange.drop(1)

                    val song = album.tracks[songIndex].title
                    val artist = album.artists[0].name

                    val answers: ArrayList<String> = arrayListOf()

                    answers.addAll(myRange.take(3)
                        .stream()
                        .map { album.tracks[it].title }
                        .toList())

                    createLyricMxmQuestion(artist, song, answers)
                }
            }

            AlbumQuestions.NUMBER_TRACKS.ordinal -> {
                createAlbumNumberOfTrackQuestion(album)
            }

            AlbumQuestions.PREVIEW.ordinal -> {

                if (album.totalTracks >= 4) {
                    var myRange = (0 until album.tracks.size).toList().shuffled()

                    val songIndex = myRange.take(1)[0]
                    myRange = myRange.drop(1)

                    val previewUrl = album.tracks[songIndex].preview_url
                    if (previewUrl.isNullOrBlank()) return

                    val answers: ArrayList<String> = arrayListOf()
                    val song = album.tracks[songIndex].title


                    answers.addAll(myRange.take(3)
                        .stream()
                        .map { album.tracks[it].title}
                        .toList())

                    createSongPreviewFromAlbumQuestion(album.title, song, previewUrl, answers)
                }


            }

            AlbumQuestions.REFERENT.ordinal -> {
                //TODO
            }

            AlbumQuestions.SONG_DESC.ordinal -> {
                if (album.totalTracks >= 4) {
                    var myRange = (0 until album.tracks.size).toList().shuffled()

                    val songIndex = myRange.take(1)[0]
                    myRange = myRange.drop(1)


                    val song = album.tracks[songIndex].title
                    val artist = album.artists[0].name

                    val answers: ArrayList<String> = arrayListOf()

                    answers.addAll(
                        myRange.take(3)
                            .stream()
                            .map { album.tracks[it].title }
                            .toList())

                    createSongDescriptionQuestion(artist, song, answers)
                }
            }

            AlbumQuestions.YEAR.ordinal -> {
                createAlbumYearQuestion(album)
            }
        }
    }

    /**
     * Creates a question where the user needs to guess the song based on its lyrics using the Musixmatch API.
     *
     * @param artist The name of the artist.
     * @param song The title of the song.
     * @param answers The list of answer options for the question.
     */
    private suspend fun createLyricMxmQuestion(
        artist: String,
        song: String,
        answers: ArrayList<String>
    ) {

        val lyricsFlow = lyricsSongMXMQuestion(artist, song)
        lyricsFlow.collect { resource ->
            when (resource) {
                is Resource.Success -> {
                    val lyrics = resource.data!!

                    val index = (0..3).random()
                    answers.add(index, song)

                    val question = Question(
                        question = "$lyrics\n This is the lyrics of which song?",
                        answers = answers,
                        right = index,
                        timeToAnswer = 20.seconds
                    )
                    val qList = _stateGame.value.questions
                    qList.add(question)
                    _stateGame.value = _stateGame.value.copy(questions = qList)
                }
                is Resource.Error -> {
                    Log.d("GameMaster", resource.message!!)
                }
                is Resource.Loading -> {
                    Log.d("GameMaster", "Loading lyrics question")
                }
            }
        }
    }

    /**
     * Creates a question where the user needs to guess the song based on its description using a custom API.
     *
     * @param artist The name of the artist.
     * @param song The title of the song.
     * @param answers The list of answer options for the question.
     */
    private suspend fun createSongDescriptionQuestion(
        artist: String,
        song: String,
        answers: ArrayList<String>
    ) {

        val descriptionFlow = descriptionSongQuestion(artist, song)
        descriptionFlow.collect { resource ->
            when (resource) {
                is Resource.Success -> {
                    val description = resource.data!!

                    val index = (0..3).random()
                    answers.add(index, song)

                    val question = Question(
                        question = "$description\n This is the description of which song?",
                        answers = answers,
                        right = index,
                        timeToAnswer = 30.seconds
                    )
                    val qList = _stateGame.value.questions
                    qList.add(question)
                    _stateGame.value = _stateGame.value.copy(questions = qList)
                }
                is Resource.Error -> {
                    Log.d("GameMaster", resource.message!!)
                }
                is Resource.Loading -> {
                    Log.d("GameMaster", "Loading description question")
                }
            }
        }
    }


    /**
     * Creates a question where the user needs to guess the song based on its preview URL from an album.
     *
     * @param from The source of the question, such as the playlist or album name.
     * @param song The title of the song.
     * @param previewUrl The URL of the song preview.
     * @param answers The list of answer options for the question.
     */
    private fun createSongPreviewFromAlbumQuestion(
        from: String,
        song: String,
        previewUrl: String,
        answers: ArrayList<String>
    ) {
        val index = (0..3).random()
        answers.add(index, song)

        val question = Question(
            question = "Can you name this song from ${from}?",
            songURL = previewUrl,
            answers = answers,
            right = index,
            timeToAnswer = 40.seconds
        )

        val qList = _stateGame.value.questions
        qList.add(question)
        _stateGame.value = _stateGame.value.copy(questions = qList)
    }

    /**
     * Create the question that ask the user to guess the number of tracks of the album
     *
     * @param album: the info of the album
     */
    private fun createAlbumNumberOfTrackQuestion(album: Album) {
        val answers: ArrayList<String> = arrayListOf()
        val number = album.tracks.size

        val range = ((
                if (number > 5) -5 else -number
                )..-1) + (1..(5))

        answers.addAll(range.shuffled().take(3)
            .stream()
            .map { (number + it).toString() }
            .toList())

        val index = (0..3).random()
        answers.add(index, number.toString())

        val question = Question(
            question = "How many tracks the album ${album.title} has",
            answers = answers,
            right = index,
            timeToAnswer = 10.seconds
        )
        val qList = _stateGame.value.questions
        qList.add(question)
        _stateGame.value = _stateGame.value.copy(questions = qList)
    }

    /**
     * Create the question that ask the user to guess the release date of the album
     *
     * @param album: the info of the album
     */
    private fun createAlbumYearQuestion(album: Album) {

        val answers: ArrayList<String> = arrayListOf()
        val date = album.releaseDate.split("-")[0].toInt()

        val diff = Calendar.getInstance().get(Calendar.YEAR) - date

        val range =
            if (diff > 0) {
                ((-5)..-1) + (1..diff)
            } else (((-5)..-1))

        answers.addAll(range.shuffled().take(3)
            .stream()
            .map { (date + it).toString() }
            .toList())

        val index = (0..3).random()
        answers.add(index, date.toString())

        val question = Question(
            question = "What is the release date of the album: ${album.title}",
            answers = answers,
            right = index,
            timeToAnswer = 20.seconds
        )

        val qList = _stateGame.value.questions
        qList.add(question)
        _stateGame.value = _stateGame.value.copy(questions = qList)
    }

    /**
     * Based on question type and artist index create a question.
     *
     * @param questionType: the type of question to create.
     * @param artistIndex: the index of the artist in the userArtist array.
     */
    private suspend fun createArtistsQuestions(questionType: Int, artistIndex: Int) {

        val artist = userArtists[artistIndex]

        when (questionType) {
            ArtistQuestions.IMAGE.ordinal -> {
                if (artist.images.isNotEmpty()) {
                    val answers = ArrayList(userArtists.stream()
                        .map { it.name }
                        .filter { it != artist.name }
                        .toList().shuffled().take(3))

                    createArtistImageQuestion(artist.images[0], artist.name, answers)
                }
            }

            ArtistQuestions.GENRE.ordinal -> {
                if (artist.genres.isNotEmpty())
                    createArtistGenresQuestion(artist.name, artist.genres)
            }

            ArtistQuestions.DESCRIPTION.ordinal -> {
                val answers = ArrayList(userArtists.stream()
                    .map { it.name }
                    .filter { it != artist.name }
                    .toList().shuffled().take(3))
                createArtistDescriptionQuestion(artist.name, answers)
            }

            ArtistQuestions.ALBUM_NUMBER.ordinal -> {
                createArtistNumberOfAlbumQuestion(artist)
            }
        }
    }

    /**
     * Create the question that ask the user to guess the number of albums published by the artist
     *
     * @param artist: the info of the artist
     */
    private suspend fun createArtistNumberOfAlbumQuestion(artist: Artist) {

        val album = numberAlbumArtistQuestion(artist.id)
        album.collect { resource ->
            when (resource) {
                is Resource.Success -> {

                    val answers: ArrayList<String> = arrayListOf()
                    val number = resource.data!!

                    val range = ((
                            if (number > 5) -5 else -number
                            )..-1) + (1..(5))

                    answers.addAll(range.shuffled().take(3)
                        .stream()
                        .map { (number + it).toString() }
                        .toList())

                    val index = (0..3).random()
                    answers.add(index, number.toString())

                    val question = Question(
                        question = "How many album has been published by ${artist.name}",
                        answers = answers,
                        right = index,
                        timeToAnswer = 10.seconds
                    )
                    val qList = _stateGame.value.questions
                    qList.add(question)
                    _stateGame.value = _stateGame.value.copy(questions = qList)
                }
                is Resource.Error -> {
                    Log.e("GameMaster", resource.message!!)
                }
                is Resource.Loading -> {
                    Log.d("GameMaster", "Loading")
                }
            }
        }


    }

    /**
     * Creates a question where the user needs to guess the artist based on a description using a custom API.
     *
     * @param artistName The name of the artist.
     * @param answers The list of answer options for the question.
     */
    private suspend fun createArtistDescriptionQuestion(
        artistName: String,
        answers: ArrayList<String>
    ) {

        val description = descriptionArtistQuestion(artist = artistName)
        description.collect { resource ->
            when (resource) {
                is Resource.Success -> {

                    val index = (0..3).random()
                    answers.add(index, artistName)

                    val question = Question(
                        question = "Who is the artist described by this paragraph:\n ${resource.data}",
                        answers = answers,
                        right = index,
                        timeToAnswer = 30.seconds
                    )

                    val qList = _stateGame.value.questions
                    qList.add(question)
                    _stateGame.value = _stateGame.value.copy(questions = qList)
                }
                is Resource.Error -> {
                    Log.e("GameMaster", resource.message!!)
                }
                is Resource.Loading -> {
                    Log.d("GameMaster", "Loading")
                }
            }
        }
    }


    /**
     * Creates a question where the user needs to guess the genres of an artist.
     *
     * @param artistName The name of the artist.
     * @param artistGenres The list of genres associated with the artist.
     */
    private fun createArtistGenresQuestion(artistName: String, artistGenres: ArrayList<String>) {

        val answers =
            ArrayList(SpotifyApiConst.genres.subtract(artistGenres.toSet()).shuffled().take(3))

        val index = (0..3).random()
        val gIndex = (0 until artistGenres.size).random()
        answers.add(index, artistGenres[gIndex])

        val question = Question(
            question = "What are the genres of $artistName",
            answers = answers,
            right = index,
            timeToAnswer = 10.seconds
        )

        val qList = _stateGame.value.questions
        qList.add(question)
        _stateGame.value = _stateGame.value.copy(questions = qList)
    }

    /**
     * Creates a question where the user needs to identify the artist from an image.
     *
     * @param artistImage The URL of the artist's image.
     * @param artistName The name of the artist.
     * @param answers The list of answer options for the question.
     */
    private fun createArtistImageQuestion(
        artistImage: String,
        artistName: String,
        answers: ArrayList<String>
    ) {

        val index = (0..3).random()
        answers.add(index, artistName)

        val question = Question(
            question = "Who is this artist?",
            imageURL = artistImage,
            answers = answers,
            right = index,
            timeToAnswer = 20.seconds
        )

        val qList = _stateGame.value.questions
        qList.add(question)
        _stateGame.value = _stateGame.value.copy(questions = qList)
    }

    /**
     * Rotates the card between front and back views.
     */
    private fun rotateCard() {
        if (stateGame.value.rotationCard == 0f) {
            _stateGame.value = _stateGame.value.copy(rotationCard = 180f)
        } else {
            _stateGame.value = _stateGame.value.copy(rotationCard = 0f)
        }
    }

    /**
     * Saves the quiz completion status to the database.
     *
     * @param completed Indicates whether the quiz is completed or not.
     */
    fun saveQuizDb(completed: Boolean) {
        viewModelScope.launch {
            updateQuizUseCase(
                Quiz(
                    collection = _stateGame.value.gameType.category,
                    id = _stateGame.value.gameType.keyword,
                    completed = completed
                )
            )
        }
    }

    /**
     * Sets the readiness status of the game.
     *
     * @param ready Indicates whether the game is ready to start or not.
     */
    fun setReady(ready: Boolean) {
        _stateGame.value = _stateGame.value.copy(isAnswered = false)
        _stateGame.value = _stateGame.value.copy(readyToStart = ready)
    }

    /**
     * Saves the endless quiz score to the database.
     */
    fun saveEndlessDb() {
        viewModelScope.launch {
            updateEndlessUseCase(
                EndlessQuiz(
                    collection = _stateGame.value.gameType.category,
                    id = _stateGame.value.gameType.keyword,
                    score = _stateGame.value.score,
                )
            )
        }
    }

    /**
     * Starts the client connection to the server.
     */
    private fun startClient() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val serverAddress = "93.43.226.194"
                    val serverPort = 30000

                    clientSocket = Socket(serverAddress, serverPort)

                    val reader = BufferedReader(InputStreamReader(clientSocket!!.getInputStream()))
                    writer = BufferedWriter(
                        OutputStreamWriter(clientSocket!!.getOutputStream()),
                        DEFAULT_BUFFER_SIZE * 3
                    )

                    var response: String? = reader.readLine()
                    while (response != null) {
                        val index = response.indexOf("---")
                        var prefix: String
                        if (index >= 0) {
                            prefix = response.substring(0, index)
                            response = response.substring(index + 3) // Skip the "---"
                        } else {
                            prefix = response
                        }
                        when (prefix) {
                            "Loading" -> {
                                _stateGame.value =
                                    _stateGame.value.copy(loadingText = "Match found!\n We are generating the match questions!")
                                _stateConnection.value =
                                    _stateConnection.value.copy(isLoading = true)
                            }
                            "Generate" -> {
                                _stateGame.value =
                                    _stateGame.value.copy(loadingText = "Match found!\n We are generating the match questions!")
                                _stateConnection.value =
                                    _stateConnection.value.copy(isLoading = true)
                                getArtistFromPlaylist(_stateGame.value.gameType.idSpotify)
                                initArtistQuestions()
                                val send = toServer()
                                writer!!.write("Questions---$send" + "E-O-M")
                                writer!!.flush()
                            }
                            "Create" -> {
                                fromServer(response)
                                writer!!.write("Ready" + "E-O-M")
                                writer!!.flush()
                            }
                            "Start" -> {
                                _stateConnection.value =
                                    _stateConnection.value.copy(isLoading = false)
                            }
                            "Update" -> {
                                updateHistory(response)
                            }
                            "End" -> {
                                if (response == "true") {
                                    endGame(true)
                                } else {
                                    endGame(false)
                                }
                            }
                        }

                        if (response.contains("end connection")) {
                            break
                        }

                        response = reader.readLine()
                    }

                    clientSocket!!.close()
                } catch (e: Exception) {
                    if (clientSocket != null && !clientSocket?.isClosed!!) {
                        _stateGame.value = _stateGame.value.copy(error_occurred = true)
                        _stateGame.value =
                            _stateGame.value.copy(error_message = "Error in connection")
                    }
                }
            }
        }
    }

    /**
     * Handles the end of the game based on the win status.
     *
     * @param win Indicates whether the player won the game or not.
     */
    private fun endGame(win: Boolean) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                updateOnlineUseCase(OnlineQuiz(
                    collection = _stateGame.value.gameType.category,
                    id = _stateGame.value.gameType.keyword,
                    score = _stateGame.value.score,
                    win = win))
            }
        }
        _stateGame.value = _stateGame.value.copy(canShowEndGame = true, didIWin = win)
        stopTimer()
        resetTimer()
    }

    /**
     * Determines the winner of the game.
     *
     * @return The winner status: 0 for self win, 1 for opponent win, 2 to continue.
     */
    private fun isWinner(): Int {
        val failed = _stateGame.value.isFailed
        val otherFailed = if (_stateGame.value.onlineHistory.otherPlayerHistory.isNotEmpty()) {
            !_stateGame.value.onlineHistory.otherPlayerHistory[_stateGame.value.onlineHistory.otherPlayerCurrent - 1]
        } else {false}
        val ahead =
            (_stateGame.value.currentQuizId) > (_stateGame.value.onlineHistory.otherPlayerCurrent - 1)
        val equal =
            (_stateGame.value.currentQuizId) == (_stateGame.value.onlineHistory.otherPlayerCurrent - 1)
        val behind =
            (_stateGame.value.currentQuizId) < (_stateGame.value.onlineHistory.otherPlayerCurrent - 1)
        val finished = _stateGame.value.questions.stream().noneMatch { !it.answerGiven }

        Log.d("isWinner", "failed: $failed, otherFailed: $otherFailed, ahead: $ahead, equal: $equal, behind: $behind, finished: $finished")

        if (finished)
            return 0 //win
        if (failed){
            if (otherFailed){
                if (ahead)
                    return 0 //win
                if (equal || behind)
                    return 1 //lose
            }else{
                if (behind || equal)
                    return 1 //lose
            }
        }else{
            if (otherFailed){
                if (ahead || equal)
                    return 0 //win
            }
        }

        return 2 //keep going
    }

    /**
     * Sends the game end status to the server.
     *
     * @param win Indicates whether the player won the game or not.
     */
    private fun sendEnd(win: Boolean) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if (!clientSocket?.isClosed!! && clientSocket?.isConnected!! && writer != null) {
                    writer!!.write("End---${win}E-O-M")
                    writer!!.flush()
                }
            }
        }
    }

    /**
     * Sends the game update to the server.
     */
    private fun sendUpdate() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if (!clientSocket?.isClosed!! && clientSocket?.isConnected!! && writer != null) {
                    val history = OnlineHistory(
                        _stateGame.value.currentQuizId + 1,
                        ArrayList(_stateGame.value.questions.map { it.correctAnswerGiven })
                    )
                    val json = "Update---" + Json.encodeToString(history)
                    writer!!.write(json + "E-O-M")
                    writer!!.flush()
                }
            }
        }
    }

    /**
     * Updates the game history based on the received response from the server.
     *
     * @param response The response string containing the game history.
     */
    private fun updateHistory(response: String) {
        val history = Json.decodeFromString<OnlineHistory>(response)
        _stateGame.value = _stateGame.value.copy(onlineHistory = history)
    }

    /**
     * Converts the game state to a JSON string to send to the server.
     *
     * @return The JSON string representing the game state.
     */
    private fun toServer(): String {
        return Json.encodeToString(_stateGame.value.toSerializable())
    }

    /**
     * Updates the game state based on the received response from the server.
     *
     * @param response The response string containing the updated game state.
     */
    private fun fromServer(response: String) {
        val game = Json.decodeFromString<GameQuestion>(response)
        _stateGame.value = _stateGame.value.copy(
            gameType = game.gameType,
            questions = ArrayList(game.questions.map { it.toQuestion() }),
            readyToStart = true
        )
    }

    /**
     * Closes the client socket connection.
     */
    fun closeSocket() {
        if (clientSocket != null && !clientSocket!!.isClosed) {
            clientSocket!!.close()
        }
    }

    /**
     * Setter for the game state for testing purposes.
     */
    fun setStateGame(state: GameState) {
        _stateGame.value = state
    }

}