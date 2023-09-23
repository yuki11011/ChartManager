
import androidx.annotation.WorkerThread
import com.example.chartManager.ItemDao
import com.example.chartManager.Record

class Repository(private val itemDao: ItemDao) {
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getLatestId(): Record.QuestionIdAndTime? {
        if (itemDao.getLatestId() != null) {
            return itemDao.getLatestId()
        }
        return null
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getDataFromQuestionId(id: Float): List<Record>? {
        if (itemDao.getDataFromQuestionId(id) != null) {
            return itemDao.getDataFromQuestionId(id)
        }
        return null
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getHistory(numRecords: Int): List<Record>? {
        if (itemDao.getHistory(numRecords) != null) {
            return itemDao.getHistory(numRecords)
        }
        return null
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getReview(minDate: Long, maxDate: Long):List<Record> {
        return itemDao.getReview(minDate, maxDate)
    }

}