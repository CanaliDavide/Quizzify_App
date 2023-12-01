package com.example.quizzify.dataLayer.database

import android.util.Log
import com.example.quizzify.dataLayer.authenticator.UserData
import com.example.quizzify.dataLayer.common.Resource
import com.example.quizzify.dataLayer.database.data.*
import com.example.quizzify.ui.composable.GameType
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.patrykandpatrick.vico.core.entry.FloatEntry
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject

const val TAG = "DATABASE"

class DatabaseRepositoryImpl @Inject constructor(
    private val db: FirebaseFirestore
) : DatabaseRepository {

    /**
     * @see DatabaseRepository.getGameType
     */
    override suspend fun getGameType(category: String): Flow<Resource<List<GameType>>> =
        callbackFlow {
            Log.d(TAG, "Get game type")
            trySend(Resource.Loading(null))
            val snapshotListener: ListenerRegistration =  try {
                db.collection("quizzes")
                    .whereEqualTo("category", category)
                    .addSnapshotListener { snapshot, e ->
                        val quizList = if (snapshot != null && e == null) {
                            val quizzes = snapshot.toObjects(GameType::class.java)
                            Resource.Success(quizzes)
                        } else {
                            Resource.Error("Error in retrieving quizzes!")
                        }
                        trySend(quizList)
                        close()
                    }
            }catch (e: Exception) {
                trySend(Resource.Error("Error in retrieving quizzes!"))
                close()
                ListenerRegistration {  }
            }
            awaitClose {
                snapshotListener.remove()
            }
        }

    /**
     * @see DatabaseRepository.createUser
     */
    override suspend fun createUser(): Resource<Boolean> {
        Log.d(TAG, "Creating User")
        return try {
            db.collection("users").document(UserData.uid)
                .set(
                    hashMapOf(
                        "spotify_id" to UserData.spotifyId,
                        "username" to UserData.username,
                        "image" to UserData.image,
                        "maxScoreEndless" to 0,
                        "maxScoreOnline" to 0,
                    )
                ).await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error("Failed to create user", false)
        }
    }

    /**
     * @see DatabaseRepository.updateQuiz
     */
    override suspend fun updateQuiz(quiz: Quiz): Resource<Boolean> {
        Log.d(TAG, "Updating Quiz")
        return try {
            db.collection("users")
                .document(UserData.uid)
                .collection(quiz.collection)
                .document(quiz.id)
                .set(quiz).await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error("Failed to create user", false)
        }
    }

    /**
     * @see DatabaseRepository.updateEndlessQuiz
     */
    @Suppress("UNCHECKED_CAST")
    override suspend fun updateEndlessQuiz(quiz: EndlessQuiz): Resource<Boolean> {
        Log.d("Endless", "Updating Endless Quiz")
        return try {
            val user = db.collection("users")
                .document(UserData.uid)

            val quizDB = user.collection(quiz.collection)
                .document(quiz.id)


            quizDB.get().addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val scores = document.get("scores") as ArrayList<Float>
                    scores.add(quiz.score.toFloat())
                    quizDB.update("scores", scores)
                    val maxScore = document.getDouble("maxScore")
                    if (maxScore != null) {
                        if (maxScore < quiz.score) {
                            quizDB.update("maxScore", quiz.score)
                            user.update("maxScoreEndless", quiz.score)
                        }
                    } else {
                        quizDB.update("maxScore", quiz.score)
                        user.update("maxScoreEndless", quiz.score)
                    }
                } else {
                    Log.d("Endless", "No such document")
                    quizDB.set(
                        hashMapOf(
                            "collection" to quiz.collection,
                            "id" to quiz.id,
                            "maxScore" to quiz.score,
                            "scores" to listOf(quiz.score)
                        )
                    )
                    user.update("maxScoreEndless", quiz.score)
                }

            }.addOnFailureListener {
                Log.d("Endless", "Error in getting the document")
                Resource.Error("Failed to create user", false)
            }

            Log.d("Endless", "Done")
            Resource.Success(true)

        } catch (e: Exception) {
            Log.d("Endless", "Error: $e")
            Resource.Error("Failed to create user", false)
        }
    }

    /**
     * @see DatabaseRepository.isToSave
     */
    override suspend fun isToSave(id: String, collection: String): Resource<Boolean> {
        try {
            val doc = db.collection("users")
                .document(UserData.uid)
                .collection(collection)
                .document(id)
                .get()
                .await()
            val save = if (doc != null && doc.exists()) {
                val completed = doc.data!!["completed"].toString()
                if (completed == "true")
                    Resource.Success(false)
                else
                    Resource.Success(true)
            } else {
                Resource.Success(true)
            }
            return (save)
        } catch (e: Exception) {
            return Resource.Error("Error in retrieving information from DataBase")
        }
    }

    /**
     * @see DatabaseRepository.getQuizzes
     */
    override suspend fun getQuizzes(
        quizCollection: String
    ): Flow<Resource<List<Quiz>>> = callbackFlow {
        Log.d(TAG, "Getting Quiz")
        trySend(Resource.Loading(null))
        val snapshotListener: ListenerRegistration = try {
            db.collection("users")
                .document(UserData.uid)
                .collection(quizCollection)
                .whereEqualTo("completed", true)
                .addSnapshotListener { snapshot, e ->
                    val quizList = if (snapshot != null && e == null) {
                        val quizzes = snapshot.toObjects(Quiz::class.java)
                        Resource.Success(quizzes)
                    } else {
                        Resource.Error("Error in retrieving quizzes")
                    }
                    trySend(quizList)
                    close()
                }
        } catch (e: Exception) {
            trySend(Resource.Error("Error in retrieving quizzes"))
            close()
            ListenerRegistration { }
        }
        awaitClose {
            snapshotListener.remove()
        }
    }

    /**
     * @see DatabaseRepository.getScores
     */
    @Suppress("UNCHECKED_CAST")
    override suspend fun getScores(game: String): Flow<Resource<CompetitiveResponse>> =
        callbackFlow {
            trySend(Resource.Loading(null))
            val snapshotListener: ListenerRegistration = try {
                db.collection("users")
                    .document(UserData.uid)
                    .collection(game.uppercase(Locale.getDefault()))
                    .addSnapshotListener { snapshot, e ->
                        val scores =
                            if (snapshot != null && e == null) {
                                if (snapshot.documents.isEmpty()){
                                    val response = CompetitiveResponse(
                                        collection = game.uppercase(Locale.getDefault()),
                                        id = game.uppercase(Locale.getDefault()),
                                        maxScore = 0.0,
                                        scores = arrayListOf(FloatEntry(0f, 0f)),
                                    )
                                    Resource.Success(response)
                                }else {
                                    val doc = snapshot.documents[0]
                                    val scores = doc.get("scores") as ArrayList<Float>
                                    val myArray: ArrayList<FloatEntry> = arrayListOf()
                                    for (i in 0 until scores.size) {
                                        val x: Float = i.toFloat()
                                        val y: Float = scores[i]
                                        myArray.add(i, FloatEntry(x, y))
                                    }

                                    val response = CompetitiveResponse(
                                        collection = doc.getString("collection")!!,
                                        id = doc.getString("id")!!,
                                        maxScore = doc.getDouble("maxScore")!!,
                                        scores = myArray,
                                    )
                                    Log.d("RANKING", "Endless response: $response")
                                    Resource.Success(response)
                                }
                            } else {
                                Log.d("RANKING", "Error $e")
                                Resource.Error("Error in retrieving scores")
                            }
                        trySend(scores)
                        close()
                    }
            } catch (e: Exception) {
                trySend(Resource.Error("Error in retrieving scores"))
                close()
                ListenerRegistration { }
            }
            awaitClose {
                snapshotListener.remove()
            }
        }

    /**
     * @see DatabaseRepository.getRanking
     */
    override suspend fun getRanking(game: String): Flow<Resource<ArrayList<Rank>>> = callbackFlow {
        trySend(Resource.Loading(null))
        val snapshotListener: ListenerRegistration = try {
            db.collection("users")
                .orderBy("maxScore$game", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, e ->
                    if (e == null) {
                        val ranking = arrayListOf<Rank>()
                        Log.d("RANKING", "Ranking")
                        for ((index, document) in snapshot!!.documents.withIndex()) {
                            val isMe = document.id == UserData.uid
                            val username = document.getString("username")
                            val image = document.getString("image")
                            val maxScore = document.getDouble("maxScore$game")
                            Log.d(
                                "RANKING",
                                "Username: $username, Image: $image, Max Score: $maxScore"
                            )
                            if (username != null && image != null && maxScore != null) {
                                Log.d(
                                    "RANKING",
                                    "Username: $username, IsMe: $isMe, Image: $image, Max Score: $maxScore"
                                )
                                ranking.add(Rank(isMe, username, image, maxScore, index))
                            }
                        }
                        for (i in ranking.size until 9) {
                            ranking.add(
                                i,
                                Rank(
                                    false,
                                    "None",
                                    "https://upload.wikimedia.org/wikipedia/en/thumb/3/3b/SpongeBob_SquarePants_character.svg/640px-SpongeBob_SquarePants_character.svg.png",
                                    0.0,
                                    i
                                )
                            )
                        }
                        trySend(Resource.Success(ranking))
                        close()
                    } else {
                        trySend(Resource.Error("Error in retrieving ranking"))
                        close()
                    }
                }
        } catch (e: Exception) {
            trySend(Resource.Error("Error in retrieving ranking"))
            close()
            ListenerRegistration { }
        }
        awaitClose {
            snapshotListener.remove()
        }

    }

    /**
     * @see DatabaseRepository.updateOnlineQuiz
     */
    @Suppress("UNCHECKED_CAST")
    override suspend fun updateOnlineQuiz(quiz: OnlineQuiz): Resource<Boolean> {
        Log.d("Endless", "Updating Endless Quiz")
        return try {
            val user = db.collection("users")
                .document(UserData.uid)

            val quizDB = user.collection(quiz.collection)
                .document(quiz.id)


            quizDB.get().addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val scores = document.get("scores") as ArrayList<Float>
                    scores.add(quiz.score.toFloat())
                    quizDB.update("scores", scores)
                    val maxScore = document.getDouble("maxScore")
                    if (maxScore != null) {
                        if (quiz.win) {
                            quizDB.update("maxScore", maxScore + 1)
                            user.update("maxScoreOnline", maxScore + 1)
                        }else{
                            quizDB.update("maxScore", maxScore)
                            user.update("maxScoreOnline", maxScore)
                        }
                    } else {
                        if (quiz.win) {
                            quizDB.update("maxScore", 1)
                            user.update("maxScoreOnline", 1)
                        }else{
                            quizDB.update("maxScore", 0)
                            user.update("maxScoreOnline", 0)
                        }
                    }
                } else {
                    Log.d("Online", "No such document")
                    quizDB.set(
                        hashMapOf(
                            "collection" to quiz.collection,
                            "id" to quiz.id,
                            "maxScore" to if (quiz.win) 1 else 0,
                            "scores" to listOf(quiz.score)
                        )
                    )
                    user.update("maxScoreOnline", if (quiz.win) 1 else 0)
                }

            }.addOnFailureListener {
                Log.d("Online", "Error in getting the document")
                Resource.Error("Failed to update Online Quiz", false)
            }

            Log.d("Online", "Done")
            Resource.Success(true)

        } catch (e: Exception) {
            Log.d("Online", "Error: $e")
            Resource.Error("Failed to update Online Quiz", false)
        }
    }
}