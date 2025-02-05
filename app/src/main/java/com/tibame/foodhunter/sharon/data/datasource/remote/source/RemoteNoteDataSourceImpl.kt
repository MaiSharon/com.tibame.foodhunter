package com.tibame.foodhunter.sharon.data.datasource.remote.source

import android.util.Log
import com.google.gson.JsonSyntaxException
import com.tibame.foodhunter.core.data.networking.NetworkCaller
import com.tibame.foodhunter.sharon.data.datasource.base.NoteDataSource
import com.tibame.foodhunter.sharon.data.datasource.remote.api.NoteApiService
import com.tibame.foodhunter.sharon.data.datasource.remote.model.NoteDto
import com.tibame.foodhunter.core.domain.util.DataError
import com.tibame.foodhunter.core.domain.util.Result
import com.tibame.foodhunter.core.domain.util.map
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class RemoteNoteDataSourceImpl @Inject constructor(
    private val noteApiService: NoteApiService,
    private val networkCaller: NetworkCaller
) : NoteDataSource {
    override suspend fun getNotes(): Result<List<NoteDto>, DataError.Network> {
        return networkCaller.execute {
            noteApiService.getNotes()
        }
    }
    override suspend fun getNote(noteId:Int): Result<NoteDto, DataError.Network> {
        return networkCaller.execute {
            noteApiService.getNote(noteId)
        }
    }
}

