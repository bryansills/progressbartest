package ninja.bryansills.progressbartest.database

import androidx.room.RoomDatabase
import androidx.room.withTransaction

class UserInteractor(val userQuerier: UserQuerier, val database: RoomDatabase) {
    suspend fun addSomeUsers(count: Int) {
        database.withTransaction {
            (1..count).forEach { userQuerier.add(User(it, "First Name $it", "Last Name $it")) }
        }
    }
}
