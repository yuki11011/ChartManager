package com.example.chartManager

import android.content.Context
import androidx.room.*
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.sql.Time
import java.text.SimpleDateFormat
import java.time.LocalDate

@Entity(tableName = "records")
data class Record(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val question_id: Float,
    val date: Long,
    val time: Int
) {
    data class QuestionIdAndTime(
        val question_id: Float, val time: String
    )
}

@Database(entities = [Record::class], version = 3, exportSchema = false)
abstract class ItemDatabase : RoomDatabase() {

    abstract fun itemDao(): ItemDao

    private class WordDatabaseCallback(
        private val scope: CoroutineScope
    ) : Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.itemDao())
                }
            }
        }

        suspend fun populateDatabase(itemDao: ItemDao) {
            // TODO: Add your own words!
        }
    }

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: ItemDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): ItemDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ItemDatabase::class.java,
                    "item_database"
                )
                    .addCallback(WordDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}

@Dao
interface ItemDao {
    //for cumulative data
    @Query("SELECT * FROM records WHERE question_id = :id ORDER BY date DESC")
    suspend fun getDataFromQuestionId(id: Float): List<Record>

    @Query("DELETE FROM records WHERE id = :id")
    suspend fun deleteDataOfId(id: Int)

    @Query("INSERT INTO records(question_id, date, time) VALUES(:id, :date, :time)")
    suspend fun insertData(id: Int, date: Long, time: Int)

    @Query("SELECT * FROM records ORDER BY question_id ASC")
    fun getAllData(): List<Record>

    @Query("SELECT question_id, time FROM records WHERE date = (SELECT MAX(date) FROM records)")
    fun getLatestId(): Record.QuestionIdAndTime

    @Query("SELECT * FROM records ORDER BY date ASC LIMIT :numRecords")
    fun getHistory(numRecords: Int): List<Record>
}