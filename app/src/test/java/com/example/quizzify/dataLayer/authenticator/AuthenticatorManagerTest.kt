package com.example.quizzify.dataLayer.authenticator

import com.example.quizzify.dataLayer.database.DatabaseRepositoryImpl
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class AuthenticatorManagerTest {

    private lateinit var authenticatorManager: AuthenticatorManager
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseRepository: DatabaseRepositoryImpl

    @Before
    fun setup() {
        firebaseAuth = mock()
        databaseRepository = mock()
        authenticatorManager = AuthenticatorManager
        authenticatorManager.setup(databaseRepository, firebaseAuth)
    }

    @Test
    fun startTest(){
        authenticatorManager.reset()
        assertNull(authenticatorManager.firebaseAuth)
        assertNull(authenticatorManager.database)
    }

    @Suppress("UNCHECKED_CAST")
    @Test
    fun logInFromSpotifyTest() {
        // Arrange
        val id = "spotifyUserId"
        val password = "password"
        val currentUser = mock<FirebaseUser>()
        val mockTask = mock(Task::class.java) as Task<AuthResult>

        whenever(mockTask.isComplete).thenReturn(true)
        whenever(mockTask.exception).thenReturn(null)
        whenever(mockTask.isCanceled).thenReturn(false)
        whenever(mockTask.result).thenReturn(null)

        whenever(currentUser.uid).thenReturn("uid")
        whenever(firebaseAuth.currentUser).thenReturn(currentUser)
        whenever(firebaseAuth.signInWithEmailAndPassword(anyString(), anyString())).thenReturn(
            mockTask
        )
        // Act
        runBlocking {
            authenticatorManager.logInFromSpotify(id, password)
        }

        // Assert
        verify(firebaseAuth).signInWithEmailAndPassword("$id@quizzify.com", password)
        assertEquals(id, UserData.spotifyId)
        assertEquals(UserData.uid, "uid")
    }

    @Suppress("UNCHECKED_CAST")
    @Test
    fun createUserTest() {
        // Arrange
        val id = "spotifyUserId"
        val password = "password"
        val e2 = RuntimeException()
        val currentUser = mock<FirebaseUser>()
        val mockTask = mock(Task::class.java) as Task<AuthResult>

        whenever(mockTask.isComplete).thenReturn(true)
        whenever(mockTask.exception).thenReturn(null)
        whenever(mockTask.isCanceled).thenReturn(false)
        whenever(mockTask.result).thenReturn(null)

        whenever(currentUser.uid).thenReturn("uid")
        whenever(firebaseAuth.currentUser).thenReturn(currentUser)

        whenever(firebaseAuth.signInWithEmailAndPassword(anyString(), anyString())).thenThrow(e2)
        whenever(firebaseAuth.createUserWithEmailAndPassword(anyString(), anyString())).thenReturn(
            mockTask
        )

        runBlocking {
            authenticatorManager.logInFromSpotify(id, password)
        }

        assertEquals(id, UserData.spotifyId)
        assertEquals(UserData.uid, "uid")
        runBlocking {
            verify(databaseRepository).createUser()
        }

    }


    @Suppress("UNCHECKED_CAST")
    @Test
    fun createUserError1Test() {
        // Arrange
        val id = "spotifyUserId"
        val password = "password"
        val e2 = RuntimeException()
        val currentUser = mock<FirebaseUser>()
        val mockTask = mock(Task::class.java) as Task<AuthResult>

        whenever(mockTask.isComplete).thenReturn(true)
        whenever(mockTask.exception).thenReturn(null)
        whenever(mockTask.isCanceled).thenReturn(false)
        whenever(mockTask.result).thenReturn(null)

        whenever(currentUser.uid).thenReturn("uid")
        whenever(firebaseAuth.currentUser).thenReturn(null)

        whenever(firebaseAuth.signInWithEmailAndPassword(anyString(), anyString())).thenThrow(e2)
        whenever(firebaseAuth.createUserWithEmailAndPassword(anyString(), anyString())).thenReturn(
            mockTask
        )

        assertThrows(
            RuntimeException::class.java
        )
        {
            runBlocking {
                authenticatorManager.logInFromSpotify(id, password)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    @Test
    fun createUserError2Test() {
        // Arrange
        val id = "spotifyUserId"
        val password = "password"
        val e2 = RuntimeException()
        val currentUser = mock<FirebaseUser>()
        val mockTask = mock(Task::class.java) as Task<AuthResult>

        whenever(mockTask.isComplete).thenReturn(true)
        whenever(mockTask.exception).thenReturn(null)
        whenever(mockTask.isCanceled).thenReturn(false)
        whenever(mockTask.result).thenReturn(null)

        whenever(currentUser.uid).thenReturn("uid")
        whenever(firebaseAuth.currentUser).thenReturn(null)

        whenever(firebaseAuth.signInWithEmailAndPassword(anyString(), anyString())).thenThrow(e2)
        whenever(firebaseAuth.createUserWithEmailAndPassword(anyString(), anyString())).thenThrow(
            e2
        )

        assertThrows(
            RuntimeException::class.java
        )
        {
            runBlocking {
                authenticatorManager.logInFromSpotify(id, password)
            }
        }
    }


}
