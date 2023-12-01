package com.example.quizzify.domainLayer.dbUseCase

import com.example.quizzify.dataLayer.common.Resource
import com.example.quizzify.dataLayer.database.DatabaseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class IsToSaveUseCase @Inject constructor(
    private val db: DatabaseRepository
) {
    suspend operator fun invoke(id: String, collection: String): Flow<Resource<Boolean>> =
        flow {
            emit(Resource.Loading())
            val res = db.isToSave(id, collection)
            emit(res)
        }
}