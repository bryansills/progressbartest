package ninja.bryansills.progressbartest

import androidx.arch.core.executor.testing.CountingTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import ninja.bryansills.progressbartest.database.AppDatabase
import ninja.bryansills.progressbartest.database.User
import ninja.bryansills.progressbartest.database.UserInteractor
import ninja.bryansills.progressbartest.database.UserQuerier
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserInteractorTest {
    @get:Rule
    val countingTaskExecutorRule = CountingTaskExecutorRule()

    @get:Rule
    val coroutineRule = TestCoroutineRule()

    @Test
    fun simpleTest() = runBlocking {
        val userQuerier = FakeUserQuerier()
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val database = Room.inMemoryDatabaseBuilder(appContext, AppDatabase::class.java)
            .build()

        val userInteractor = UserInteractor(userQuerier, database)
        userInteractor.addSomeUsers(10)

        Assert.assertEquals(10, userQuerier.countAdded)
    }
}

class FakeUserQuerier : UserQuerier {
    var countAdded = 0

    override suspend fun add(user: User) {
        countAdded += 1
    }
}
