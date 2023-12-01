package com.example.quizzify.dataLayer.database

import com.example.quizzify.dataLayer.common.Resource
import com.example.quizzify.dataLayer.database.data.*
import com.example.quizzify.ui.composable.GameType
import kotlinx.coroutines.flow.Flow

interface DatabaseRepository {

    /**
     * Creates a user in the database with the necessary user data.
     *
     * @return A Resource object indicating the success or failure of user creation.
     */
    suspend fun createUser(): Resource<Boolean>

    /**
     * Updates a quiz in the database with the provided quiz object.
     *
     * @param quiz The Quiz object containing the updated quiz data.
     * @return A Resource object indicating the success or failure of quiz update.
     */
    suspend fun updateQuiz(quiz: Quiz): Resource<Boolean>

    /**
     * Retrieves a list of completed quizzes from the database.
     *
     * @param quizCollection The collection name where the quizzes are stored.
     * @return A Flow of Resource containing a list of Quiz objects.
     */
    suspend fun getQuizzes(quizCollection: String): Flow<Resource<List<Quiz>>>

    /**
     * Retrieves the scores of a specific game from the database.
     *
     * @param game The game name for which to retrieve the scores.
     * @return A Flow of Resource containing the competitive response object.
     */
    suspend fun getScores(game: String): Flow<Resource<CompetitiveResponse>>

    /**
     * Updates an endless quiz in the database with the provided EndlessQuiz object.
     *
     * @param quiz The EndlessQuiz object containing the updated endless quiz data.
     * @return A Resource object indicating the success or failure of endless quiz update.
     */
    suspend fun updateEndlessQuiz(quiz: EndlessQuiz): Resource<Boolean>

    /**
     * Checks if a specific quiz with the given ID and collection needs to be saved in the database.
     *
     * @param id The ID of the quiz.
     * @param collection The collection name where the quiz is stored.
     * @return A Resource object indicating whether the quiz needs to be saved or not.
     */
    suspend fun isToSave(id: String, collection: String): Resource<Boolean>

    /**
     * Retrieves the ranking of users for a specific game from the database.
     *
     * @param game The game name for which to retrieve the ranking.
     * @return A Flow of Resource containing an array list of Rank objects.
     */
    suspend fun getRanking(game: String): Flow<Resource<ArrayList<Rank>>>

    /**
     * Updates an online quiz in the database with the provided OnlineQuiz object.
     *
     * @param quiz The OnlineQuiz object containing the updated online quiz data.
     * @return A Resource object indicating the success or failure of online quiz update.
     */
    suspend fun updateOnlineQuiz(quiz: OnlineQuiz): Resource<Boolean>

    /**
     * Retrieves a list of game types from the database based on the specified category.
     *
     * @param category The category to filter the game types.
     * @return A Flow of Resource containing a list of GameType objects.
     */
    suspend fun getGameType(category: String): Flow<Resource<List<GameType>>>
}