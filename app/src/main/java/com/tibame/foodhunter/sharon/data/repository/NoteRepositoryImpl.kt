package com.tibame.foodhunter.sharon.data.repository

import com.tibame.foodhunter.sharon.data.datasource.remote.model.NoteDto
import com.tibame.foodhunter.sharon.data.datasource.remote.source.RemoteNoteDataSourceImpl
import com.tibame.foodhunter.sharon.domain.entity.CardContentType
import com.tibame.foodhunter.sharon.domain.entity.Note
import com.tibame.foodhunter.sharon.domain.error.DataError
import com.tibame.foodhunter.sharon.domain.repository.NoteRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import com.tibame.foodhunter.sharon.domain.error.Result


// 1.1 對應當前後端結構
//data class NotesResponse(
//    val notes: List<NoteDto>
//)

//// 1.2 json數據為kotlin物件，同時處理命名符合kotlin
//data class NoteDto(
//    @SerializedName("note_id") val noteId: Int,
//    val title: String,
//    val content: String,
//    @SerializedName("restaurant_id") val restaurantId: Int,
//    @SerializedName("member_id") val memberId: Int,
//    @SerializedName("selected_date") val selectedDate: String
//)


//// 轉換工具
//private val gson = Gson()

//interface INoteRepository {
//    val notes: StateFlow<List<Note>>
//
//    suspend fun getNotes(memberId: String):Flow<Result<List<Note>>> // Response的庫要使用retrofit2
//}
typealias NoteResult = Result<List<Note>, DataError.Network>

class RealNoteRepositoryImpl @Inject constructor(
    private val remoteNoteDataSourceImpl: RemoteNoteDataSourceImpl,
) : NoteRepository {

    override suspend fun getNotes(memberId: Int): NoteResult {
        return when (val response = remoteNoteDataSourceImpl.getNotes(memberId)) {
            is Result.Success -> {
                try {
                    val domainNotes = response.data.map { noteDto -> noteDto.toNote() }
                    Result.Success(domainNotes)
                } catch (e: IllegalArgumentException) {
                    Result.Error(DataError.Network.DOMAIN_CONVERSION_ERROR)
                }
            }
            is Result.Error -> Result.Error(response.error)  // 傳遞data source層的錯誤
        }
    }
}

//    class FakeNoteRepositoryImpl : NoteRepository {
//        private val _notes = MutableStateFlow<List<Note>>(emptyList())
////    override val notes: StateFlow<List<Note>> = _notes
//
//        private val testNotes =
//            listOf(
//                Note(
//                    type = CardContentType.NOTE,
//                    title = "Daily Journal",
//                    noteId = 1,
//                    selectedDate = Date(),
//                    date = "2024-12-01",
//                    day = "Monday",
//                    content = "Today was a productive day, I completed all my tasks on time.",
//                    imageResId = null,
//                    restaurantName = null,
//                    memberId = 101
//                ),
//                Note(
//                    type = CardContentType.NOTE,
//                    title = "Dinner Night",
//                    noteId = 2,
//                    selectedDate = Date(),
//                    date = "2024-12-02",
//                    day = "Tuesday",
//                    content = "Had an amazing dinner at a cozy restaurant with friends.",
//                    imageResId = null,
//                    restaurantName = null,
//                    memberId = 102
//                ),
//                Note(
//                    type = CardContentType.NOTE,
//                    title = "Team Meeting",
//                    noteId = 3,
//                    selectedDate = Date(),
//                    date = "2024-12-03",
//                    day = "Wednesday",
//                    content = "Discussed project updates and set new milestones.",
//                    imageResId = null,
//                    restaurantName = null,
//                    memberId = 103
//                )
//            )
//
//
//        override suspend fun getNotes(memberId: String): NoteResult {
//            // TODO()
//        }
//    }

//class RealNoteRepositoryImpl : INoteRepository {
//    companion object {
//        private const val TAG = "NoteRepository_new"
//    }
//
//    // 內部修改用 /UI層的note
//    private val _notes = MutableStateFlow<List<Note>>(emptyList())
//
//    // 外部只能看 / UI層的note
//    override val notes: StateFlow<List<Note>> = _notes.asStateFlow()
//
//    override suspend fun getNotes(): List<Note>{
//        try {
//            val url = ApiConfig.getNotesUrl()
//
//            // 發送請求接收響應
//            val jsonResponse = CommonPost(url, "")
//
//            // 反序列化轉換成NotesResponse類型
//            val parsedResponse = gson.fromJson(jsonResponse, NotesResponse::class.java)
//            val notes = parsedResponse.notes
//            // 每個元素轉換成符合ui層的數據結構
//            _notes.value = notes.map { it.toNote() }
//
//        } catch (e: Exception) {
//            Log.d(TAG, "錯誤", e)
//        }
//    }
//}
//
//
//// 假如依賴注入的配置完成就能這樣來調用開發或發佈環境切換的代碼 以vm舉例
//class testVM(
//    private val repository: INoteRepository = when (AppConfig.isTestMode) {
//        true -> FakeNoteRepositoryImpl()
//        false -> RealNoteRepositoryImpl()
//    }
//
//):ViewModel() {
//}

    // 1.3 NoteDto 轉換成 Note
    private fun NoteDto.toNote(): Note {
        return Note(
            type = CardContentType.NOTE,
            title = title,
            noteId = noteId,
            selectedDate = Date(),
            date = parseDate(selectedDate).toFormatDate(),
            day = parseDate(selectedDate).toFormatDayOfWeek(),
            content = content,
            memberId = memberId
        )
    }

    // 1.3.1 日期轉換格式
    private fun parseDate(dateString: String): Date {
        return try {
            SimpleDateFormat(
                "yyyy-MM-dd",       // 2024-03-20
                Locale.getDefault()   // 使用系統設定的預設地區
            ).parse(dateString) ?: Date() // .parse() 方法：把字串轉換成 Date 物件
        } catch (e: Exception) {
            Date()
        }
    }

    private fun Date.toFormatDate(): String {
        return SimpleDateFormat("MM-dd").format(this)
    }

    private fun Date.toFormatDayOfWeek(): String {
        return SimpleDateFormat("EEE").format(this)
    }
