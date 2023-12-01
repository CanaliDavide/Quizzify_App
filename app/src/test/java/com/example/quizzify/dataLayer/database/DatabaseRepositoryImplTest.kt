package com.example.quizzify.dataLayer.database

import com.example.quizzify.dataLayer.common.Resource
import com.example.quizzify.dataLayer.database.data.*
import com.example.quizzify.ui.composable.GameType
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.*
import org.mockito.kotlin.whenever
import java.util.ArrayList


class DatabaseRepositoryImplTest {

    private val db = mock(FirebaseFirestore::class.java)
    private val databaseRepositoryImpl = DatabaseRepositoryImpl(db)
    private val collectionReference = mock(CollectionReference::class.java)
    private val collectionReferencekk = mockk<CollectionReference>(relaxed = true)

    private val query = mockk<Query>()
    private val snapshot = mock(QuerySnapshot::class.java)
    private val linerRegistration = mockk<ListenerRegistration>(relaxed = true)
    private val documentReference = mock(DocumentReference::class.java)
    private val mockTaskVoid = mockk<Task<Void>>()
    private val documentSnapshot = mock(DocumentSnapshot::class.java)
    private val mockTask = mockk<Task<DocumentSnapshot>>()

    @Before
    fun setUp() {
        clearAllMocks()
        whenever(db.collection(anyString())).thenReturn(collectionReference)
        whenever(collectionReference.whereEqualTo(anyString(), any())).thenReturn(query)
        whenever(snapshot.toObjects(GameType::class.java)).thenReturn(listOf(GameType()))
        whenever(snapshot.toObjects(Quiz::class.java)).thenReturn(listOf(Quiz()))

        whenever(snapshot.documents).thenReturn(listOf(documentSnapshot))
        whenever(collectionReference.document(anyString())).thenReturn(documentReference)
        whenever(collectionReference.orderBy(anyString(), any())).thenReturn(query)

        every { mockTask.isComplete}.returns(true)
        every { mockTask.exception}.returns(null)
        every { mockTask.isCanceled}.returns(false)
        every { mockTask.result}.returns(documentSnapshot)

        every { mockTaskVoid.isComplete}.returns(true)
        every { mockTaskVoid.exception}.returns(null)
        every { mockTaskVoid.isCanceled}.returns(false)
        every { mockTaskVoid.result}.returns(null)

        whenever(documentReference.update(anyString(), any())).thenReturn(mockTaskVoid)
        whenever(documentReference.get()).thenReturn(mockTask)
        whenever(documentReference.collection(eq("B"))).thenReturn(collectionReferencekk)
        whenever(documentReference.collection(eq("A"))).thenReturn(collectionReference)

        whenever(documentSnapshot.getString(anyString())).thenReturn("test")
        whenever(documentSnapshot.get(anyString())).thenReturn(arrayListOf(1.0f))
        whenever(documentSnapshot.id).thenReturn("test")
        whenever(documentReference.set(any())).thenReturn(mockTaskVoid)

        every {mockTaskVoid.addOnSuccessListener(any())} answers {
            val listener = arg<OnSuccessListener<DocumentSnapshot>>(0)
            listener.onSuccess(documentSnapshot)
            mockTaskVoid
        }
        every {mockTaskVoid.addOnFailureListener(any())} answers {
            val listener = arg<OnFailureListener>(0)
            listener.onFailure(RuntimeException())
            mockTaskVoid
        }

        every {mockTask.addOnSuccessListener(any())} answers {
            val listener = arg<OnSuccessListener<DocumentSnapshot>>(0)
            listener.onSuccess(documentSnapshot)
            mockTask
        }
        every {mockTask.addOnFailureListener(any())} answers {
            val listener = arg<OnFailureListener>(0)
            listener.onFailure(RuntimeException())
            mockTask
        }
    }
    /**
     * Test the getGameType function when the snapshot is successful.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test getGameType success`() = runTest {
        // Mock a successful snapshot result
        every { query.addSnapshotListener(any()) } answers {
            val listener = arg<EventListener<QuerySnapshot>>(0)
            listener.onEvent(snapshot, null)
            linerRegistration
        }

        val flow: Flow<Resource<List<GameType>>> = databaseRepositoryImpl.getGameType("")
        var i = 0
        val job = launch {
            flow.collect {
                if (i == 0){
                    assert(it is Resource.Loading)
                }else if(i == 1){
                    assert(it is Resource.Success)
                }
                i++
            }
        }
        advanceUntilIdle()
        job.cancel()

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test getGameType error`() = runTest {

        every { query.addSnapshotListener(any()) } answers {
            val listener = arg<EventListener<QuerySnapshot>>(0)
            listener.onEvent(snapshot, FirebaseFirestoreException("error", FirebaseFirestoreException.Code.ABORTED))
            linerRegistration
        }

        val flow: Flow<Resource<List<GameType>>> = databaseRepositoryImpl.getGameType("")
        var i = 0
        val job = launch {
            flow.collect {
                if (i == 0){
                    assert(it is Resource.Loading)
                }else if(i == 1){
                    assert(it is Resource.Error)
                }
                i++
            }
        }
        advanceUntilIdle()
        job.cancel()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test getGameType error2`() = runTest {

        every { query.addSnapshotListener(any()) } throws RuntimeException()

        val flow: Flow<Resource<List<GameType>>> = databaseRepositoryImpl.getGameType("")
        var i = 0
        val job = launch {
            flow.collect {
                if (i == 0){
                    assert(it is Resource.Loading)
                }else if(i == 1){
                    assert(it is Resource.Error)
                }
                i++
            }
        }
        advanceUntilIdle()
        job.cancel()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test createUser success`() = runTest{

        whenever(documentReference.set(any())).thenReturn(mockTaskVoid)
        val resource = databaseRepositoryImpl.createUser()

        assert(resource is Resource.Success)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test createUser error`() = runTest{

        whenever(documentReference.set(any())).thenThrow(RuntimeException())
        val resource = databaseRepositoryImpl.createUser()

        assert(resource is Resource.Error)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test updateQuiz success`() = runTest{

        whenever(documentReference.set(any())).thenReturn(mockTaskVoid)
        val resource = databaseRepositoryImpl.updateQuiz(Quiz(collection = "A"))

        assert(resource is Resource.Success)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test updateQuiz error`() = runTest{
        whenever(documentReference.set(any())).thenThrow(RuntimeException())
        val resource = databaseRepositoryImpl.updateQuiz(Quiz(collection = "A"))

        assert(resource is Resource.Error)
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test updateEndlessQuiz success`() = runTest {
        // Mock a successful snapshot result
        whenever(documentSnapshot.getDouble(anyString())).thenReturn(1.0)
        whenever(documentSnapshot.exists()).thenReturn(true)
        val update = databaseRepositoryImpl.updateEndlessQuiz(EndlessQuiz(score = 3, collection = "A"))
        assert(update is Resource.Success)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test updateEndlessQuiz success 2`() = runTest {
        // Mock a successful snapshot result
        whenever(documentSnapshot.getDouble(anyString())).thenReturn(null)
        whenever(documentSnapshot.exists()).thenReturn(true)
        val update = databaseRepositoryImpl.updateEndlessQuiz(EndlessQuiz(score = 0, collection = "A"))
        assert(update is Resource.Success)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test updateEndlessQuiz success 3`() = runTest {
        whenever(documentSnapshot.exists()).thenReturn(false)
        val update = databaseRepositoryImpl.updateEndlessQuiz(EndlessQuiz(score = 0, collection = "A"))
        assert(update is Resource.Success)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test updateEndlessQuiz error`() = runTest {
        // Mock a successful snapshot result

        whenever(documentSnapshot.getDouble(anyString())).thenThrow(RuntimeException())
        whenever(documentSnapshot.exists()).thenReturn(true)
        val update = databaseRepositoryImpl.updateEndlessQuiz(EndlessQuiz(score = 0, collection = "A"))
        assert(update is Resource.Error)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test isToSave success`() = runTest {
        // Mock a successful snapshot result
        whenever(documentSnapshot.exists()).thenReturn(true)
        val map: MutableMap<String, Any> = hashMapOf("completed" to "true")
        whenever(documentSnapshot.data).thenReturn(map)
        val save = databaseRepositoryImpl.isToSave("a", "A")
        assert(save is Resource.Success)
        assertEquals(false, (save as Resource.Success).data)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test isToSave success 2`() = runTest {
        // Mock a successful snapshot result
        whenever(documentSnapshot.exists()).thenReturn(true)
        val map: MutableMap<String, Any> = hashMapOf("completed" to "false")
        whenever(documentSnapshot.data).thenReturn(map)
        val save = databaseRepositoryImpl.isToSave("a", "A")
        assert(save is Resource.Success)
        assertEquals(true, (save as Resource.Success).data)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test isToSave success 3`() = runTest {
        // Mock a successful snapshot result
        whenever(documentSnapshot.exists()).thenReturn(false)
        val save = databaseRepositoryImpl.isToSave("a", "A")
        assert(save is Resource.Success)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test isToSave error`() = runTest {
        // Mock a successful snapshot result
        whenever(documentSnapshot.exists()).thenReturn(true)
        whenever(documentSnapshot.data!!["completed"]).thenThrow(RuntimeException())
        val save = databaseRepositoryImpl.isToSave("a", "A")
        assert(save is Resource.Error)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test getQuizzes success`() = runTest {
        // Mock a successful snapshot result
        every { query.addSnapshotListener(any()) } answers {
            val listener = arg<EventListener<QuerySnapshot>>(0)
            listener.onEvent(snapshot, null)
            linerRegistration
        }

        val flow: Flow<Resource<List<Quiz>>> = databaseRepositoryImpl.getQuizzes("A")
        var i = 0
        val job = launch {
            flow.collect {
                if (i == 0){
                    assert(it is Resource.Loading)
                }else if(i == 1){
                    assert(it is Resource.Success)
                }
                i++
            }
        }
        advanceUntilIdle()
        job.cancel()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test getQuizzes error`() = runTest {
        // Mock a successful snapshot result
        every { query.addSnapshotListener(any()) } answers {
            val listener = arg<EventListener<QuerySnapshot>>(0)
            listener.onEvent(snapshot, FirebaseFirestoreException("a", FirebaseFirestoreException.Code.ABORTED))
            linerRegistration
        }

        val flow: Flow<Resource<List<Quiz>>> = databaseRepositoryImpl.getQuizzes("A")
        var i = 0
        val job = launch {
            flow.collect {
                if (i == 0){
                    assert(it is Resource.Loading)
                }else if(i == 1){
                    assert(it is Resource.Error)
                }
                i++
            }
        }
        advanceUntilIdle()
        job.cancel()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test getQuizzes error 2`() = runTest {
        // Mock a successful snapshot result
        every { query.addSnapshotListener(any()) } throws RuntimeException()

        val flow: Flow<Resource<List<Quiz>>> = databaseRepositoryImpl.getQuizzes("A")
        var i = 0
        val job = launch {
            flow.collect {
                if (i == 0){
                    assert(it is Resource.Loading)
                }else if(i == 1){
                    assert(it is Resource.Error)
                }
                i++
            }
        }
        advanceUntilIdle()
        job.cancel()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test getScores success`() = runTest {
        // Mock a successful snapshot result
        every { collectionReferencekk.addSnapshotListener(any()) } answers {
            val listener = arg<EventListener<QuerySnapshot>>(0)
            listener.onEvent(snapshot, null)
            linerRegistration
        }

        val flow: Flow<Resource<CompetitiveResponse>> = databaseRepositoryImpl.getScores("B")
        var i = 0
        val job = launch {
            flow.collect {
                if (i == 0){
                    assert(it is Resource.Loading)
                }else if(i == 1){
                    assert(it is Resource.Success)
                }
                i++
            }
        }
        advanceUntilIdle()
        job.cancel()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test getScores error`() = runTest {
        // Mock a successful snapshot result
        every { collectionReferencekk.addSnapshotListener(any()) } answers {
            val listener = arg<EventListener<QuerySnapshot>>(0)
            listener.onEvent(null, null)
            linerRegistration
        }

        val flow: Flow<Resource<CompetitiveResponse>> = databaseRepositoryImpl.getScores("B")
        var i = 0
        val job = launch {
            flow.collect {
                if (i == 0){
                    assert(it is Resource.Loading)
                }else if(i == 1){
                    assert(it is Resource.Error)
                }
                i++
            }
        }
        advanceUntilIdle()
        job.cancel()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test getScores error2`() = runTest {
        // Mock a successful snapshot result
        every { collectionReferencekk.addSnapshotListener(any()) } throws RuntimeException()

        val flow: Flow<Resource<CompetitiveResponse>> = databaseRepositoryImpl.getScores("B")
        var i = 0
        val job = launch {
            flow.collect {
                if (i == 0){
                    assert(it is Resource.Loading)
                }else if(i == 1){
                    assert(it is Resource.Error)
                }
                i++
            }
        }
        advanceUntilIdle()
        job.cancel()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test getRanking success`() = runTest {
        // Mock a successful snapshot result
        every { query.addSnapshotListener(any()) } answers {
            val listener = arg<EventListener<QuerySnapshot>>(0)
            listener.onEvent(snapshot, null)
            linerRegistration
        }

        whenever(documentSnapshot.getDouble(anyString())).thenReturn(1.0)

        val flow: Flow<Resource<ArrayList<Rank>>> = databaseRepositoryImpl.getRanking("A")
        var i = 0
        val job = launch {
            flow.collect {
                if (i == 0){
                    assert(it is Resource.Loading)
                }else if(i == 1){
                    assert(it is Resource.Success)
                }
                i++
            }
        }
        advanceUntilIdle()
        job.cancel()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test getRanking error`() = runTest {
        // Mock a successful snapshot result
        every { query.addSnapshotListener(any()) } answers {
            val listener = arg<EventListener<QuerySnapshot>>(0)
            listener.onEvent(snapshot, FirebaseFirestoreException("a", FirebaseFirestoreException.Code.ABORTED))
            linerRegistration
        }

        val flow: Flow<Resource<ArrayList<Rank>>> = databaseRepositoryImpl.getRanking("A")
        var i = 0
        val job = launch {
            flow.collect {
                if (i == 0){
                    assert(it is Resource.Loading)
                }else if(i == 1){
                    assert(it is Resource.Error)
                }
                i++
            }
        }
        advanceUntilIdle()
        job.cancel()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test getRanking error2`() = runTest {
        // Mock a successful snapshot result
        every { query.addSnapshotListener(any()) } throws RuntimeException()

        val flow: Flow<Resource<ArrayList<Rank>>> = databaseRepositoryImpl.getRanking("A")
        var i = 0
        val job = launch {
            flow.collect {
                if (i == 0){
                    assert(it is Resource.Loading)
                }else if(i == 1){
                    assert(it is Resource.Error)
                }
                i++
            }
        }
        advanceUntilIdle()
        job.cancel()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test updateOnlineQuiz success`() = runTest {
        // Mock a successful snapshot result
        whenever(documentSnapshot.exists()).thenReturn(true)
        whenever(documentSnapshot.getDouble(anyString())).thenReturn(1.0)
        val save = databaseRepositoryImpl.updateOnlineQuiz(OnlineQuiz(collection = "A"))
        assert(save is Resource.Success)
        assertEquals(true, (save as Resource.Success).data)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test updateOnlineQuiz success 2`() = runTest {
        // Mock a successful snapshot result
        whenever(documentSnapshot.exists()).thenReturn(true)
        whenever(documentSnapshot.getDouble(anyString())).thenReturn(null)
        val save = databaseRepositoryImpl.updateOnlineQuiz(OnlineQuiz(collection = "A"))
        assert(save is Resource.Success)
        assertEquals(true, (save as Resource.Success).data)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test updateOnlineQuiz success 3`() = runTest {
        // Mock a successful snapshot result
        whenever(documentSnapshot.exists()).thenReturn(false)
        whenever(documentSnapshot.getDouble(anyString())).thenReturn(null)
        val save = databaseRepositoryImpl.updateOnlineQuiz(OnlineQuiz(collection = "A"))
        assert(save is Resource.Success)
        assertEquals(true, (save as Resource.Success).data)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test updateOnlineQuiz error`() = runTest {
        whenever(documentSnapshot.getDouble(anyString())).thenThrow(RuntimeException())
        whenever(documentSnapshot.exists()).thenReturn(true)
        val save = databaseRepositoryImpl.updateOnlineQuiz(OnlineQuiz(collection = "A"))
        assert(save is Resource.Error)
        assertEquals(false, (save as Resource.Error).data)
    }
}