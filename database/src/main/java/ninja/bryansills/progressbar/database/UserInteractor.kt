package ninja.bryansills.progressbar.database

import androidx.room.RoomDatabase
import androidx.room.withTransaction

class UserInteractor(val userQuerier: UserQuerier, val database: RoomDatabase) {
    suspend fun addSomeUsers(count: Int) {
        database.withTransaction {
            (1..count).forEach {  }
        }
    }
}
