package ninja.bryansills.progressbar.database

import androidx.arch.core.executor.testing.CountingTaskExecutorRule
import androidx.room.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.assertEquals

@RunWith(AndroidJUnit4::class)
class UserInteractorTest {
    @get:Rule
    val countingTaskExecutorRule = CountingTaskExecutorRule()

    @get:Rule
    val coroutineRule = TestCoroutineRule()

    @Test
    fun simpleTest() = coroutineRule.dispatcher.runBlockingTest {
        val userQuerier = FakeUserQuerier()
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val database = Room.inMemoryDatabaseBuilder(appContext, MockDatabase::class.java).build()

        val userInteractor = UserInteractor(userQuerier, database)
        userInteractor.addSomeUsers(10)

        assertEquals(10, userQuerier.countAdded)
    }
}

class FakeUserQuerier : UserQuerier {
    var countAdded = 0

    override suspend fun add(user: User) {
        countAdded++
    }
}

@Database(
    entities = [MockEntity::class],
    version = 1,
    exportSchema = false
)
abstract class MockDatabase : RoomDatabase()

@Entity
data class MockEntity(@PrimaryKey(autoGenerate = true) val id: Long? = null)
