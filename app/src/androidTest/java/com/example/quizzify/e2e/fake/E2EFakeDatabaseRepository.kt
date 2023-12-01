package com.example.quizzify.e2e.fake

import com.example.quizzify.dataLayer.common.Resource
import com.example.quizzify.dataLayer.database.DatabaseRepository
import com.example.quizzify.dataLayer.database.data.CompetitiveResponse
import com.example.quizzify.dataLayer.database.data.EndlessQuiz
import com.example.quizzify.dataLayer.database.data.OnlineQuiz
import com.example.quizzify.dataLayer.database.data.Quiz
import com.example.quizzify.dataLayer.database.data.Rank
import com.example.quizzify.ui.composable.GameType
import com.google.firebase.firestore.auth.User
import com.patrykandpatrick.vico.core.entry.FloatEntry
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.Flow

class E2EFakeDatabaseRepository : DatabaseRepository {

    override suspend fun createUser(): Resource<Boolean> {
        // Simulate user creation (always successful in this fake database)
        return Resource.Success(true)
    }

    override suspend fun updateQuiz(quiz: Quiz): Resource<Boolean> {
        // Simulate quiz update (always successful in this fake database)
        return Resource.Success(true)
    }

    override suspend fun getQuizzes(quizCollection: String): Flow<Resource<List<Quiz>>> {
        // Simulate quiz retrieval for a collection
        return flowOf(Resource.Success(arrayListOf(
            Quiz(quizCollection, "id1", true),
            Quiz(quizCollection, "id2", false),
            Quiz(quizCollection, "id3", false),
        )))
    }

    override suspend fun getScores(game: String): Flow<Resource<CompetitiveResponse>> {
        // Simulate score retrieval for a game
        //array list of float entries 10, 5 ,2
        return flowOf(Resource.Success(CompetitiveResponse(game, game, 10.0, arrayListOf(
            FloatEntry(0f, 10.0F),
            FloatEntry(1f, 5.0F),
            FloatEntry(2f, 2.0F),
        ))))
    }

    override suspend fun updateEndlessQuiz(quiz: EndlessQuiz): Resource<Boolean> {
        // Simulate endless quiz update (always successful in this fake database)
        return Resource.Success(true)
    }

    override suspend fun isToSave(id: String, collection: String): Resource<Boolean> {
        // Simulate the check for whether a quiz needs to be saved
        return Resource.Success(true)
    }

    override suspend fun getRanking(game: String): Flow<Resource<ArrayList<Rank>>> {
        // Simulate ranking retrieval for a game
        //array list of ranks
        return flowOf(Resource.Success(arrayListOf(
            Rank(true, "user1", "https://i.imgur.com/1QZzQ1f.jpg", 10.0, 0),
            Rank(false, "user2", "https://i.imgur.com/1QZzQ1f.jpg", 5.0, 1),
            Rank(false, "user3", "https://i.imgur.com/1QZzQ1f.jpg", 2.0, 2),
        )))
    }

    override suspend fun updateOnlineQuiz(quiz: OnlineQuiz): Resource<Boolean> {
        // Simulate online quiz update (always successful in this fake database)
        // You can add your own logic for handling online quizzes if needed
        return Resource.Success(true)
    }

    override suspend fun getGameType(category: String): Flow<Resource<List<GameType>>> {
        // Simulate game type retrieval for a category
        return flowOf(Resource.Success(arrayListOf(
            GameType("routeImage1", "Title 1", "Description 1", "Keyword 1", category, 10, "idSpotify1", "Loading 1"),
            GameType("routeImage2", "Title 2", "Description 2", "Keyword 2", category, 10, "idSpotify2", "Loading 2"),
        )))

    }
}
