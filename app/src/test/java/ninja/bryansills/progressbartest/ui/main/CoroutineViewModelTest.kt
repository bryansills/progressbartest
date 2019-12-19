package ninja.bryansills.progressbartest.ui.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.asFlow
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CoroutineViewModelTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private val mainThreadSurrogate = newSingleThreadContext("Test thread")

    private val dispatcher = TestCoroutineDispatcher()
    private val fakeSimpleDispatcher = FakeCoroutineDispatchers(dispatcher)

    @Before
    fun setup() {
        Dispatchers.setMain(mainThreadSurrogate)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        dispatcher.cleanupTestCoroutines()
    }

    @Test
    fun fibonacci() = dispatcher.runBlockingTest {
        val viewModel = CoroutineViewModel(fakeSimpleDispatcher)

        val NUMBER_OF_STEPS = 10

        val asyncJob = async { viewModel.fibonacci.collectExactly(NUMBER_OF_STEPS) }
        viewModel.runFibonacci(NUMBER_OF_STEPS)
        val actual = asyncJob.await()

        assertEquals(NUMBER_OF_STEPS, actual.size)
        assertEquals(1, actual[0])
        assertEquals(1, actual[1])
        assertEquals(2, actual[2])
        assertEquals(3, actual[3])
        assertEquals(5, actual[4])
    }
}

suspend fun <T> LiveData<T>.collectCount(count: Int): List<T> = this.asFlow().take(count).toList()

suspend fun <T> LiveData<T>.collectExactly(count: Int): List<T> {
    val result = mutableListOf<T>()

    this.asFlow().take(count).collect { result.add(it) }

    return result
}


class FakeCoroutineDispatchers(private val dispatcher: CoroutineDispatcher) : CoroutineDispatchers {
    override val ui: CoroutineDispatcher
        get() = dispatcher

    override val computation: CoroutineDispatcher
        get() = dispatcher
}
