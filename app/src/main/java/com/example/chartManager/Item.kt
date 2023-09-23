package com.example.chartManager

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Entity(tableName = "records")
data class Record(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val question_id: Float,
    val date: Long,
    val time: Int,
    @ColumnInfo(defaultValue = "0")
    val evaluation: Byte,
    @ColumnInfo(defaultValue = "0")
    val isReviewed: Byte,
    @ColumnInfo(defaultValue = "0")
    val studyCount: Int
) {
    data class QuestionIdAndTime(
        val question_id: Float, val time: String
    )
}

@Database(entities = [Record::class], version = 7, exportSchema = false)
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
                    .fallbackToDestructiveMigration()
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
    @Query("SELECT * FROM records WHERE ROUND(question_id, 1) = ROUND(:id, 1) ORDER BY date DESC")
    suspend fun getDataFromQuestionId(id: Float): List<Record>

    @Query("DELETE FROM records WHERE id = :id")
    suspend fun deleteDataOfId(id: Int)

    @Query("INSERT INTO records(question_id, date, time) VALUES(:id, :date, :time)")
    suspend fun insertData(id: Int, date: Long, time: Int)

    @Query("SELECT * FROM records ORDER BY question_id ASC")
    fun getAllData(): List<Record>

    @Query("SELECT question_id, time FROM records WHERE date = (SELECT MAX(date) FROM records)")
    fun getLatestId(): Record.QuestionIdAndTime

    @Query("SELECT * FROM records ORDER BY date DESC LIMIT :numRecords")
    fun getHistory(numRecords: Int): List<Record>

    @Query("""
        SELECT * FROM (
            SELECT id, question_id, MAX(date) AS date, time, evaluation, isReviewed, studyCount FROM records WHERE :minDate <= date <= :maxDate and isReviewed == 0 GROUP BY question_id
        )
        ORDER BY date DESC
        """)
    suspend fun getReview(minDate: Long, maxDate:Long): List<Record>
}