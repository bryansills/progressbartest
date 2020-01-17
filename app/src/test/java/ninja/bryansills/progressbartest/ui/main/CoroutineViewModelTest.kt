package ninja.bryansills.progressbartest.ui.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.asFlow
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
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
    private val fakeSomethingRepo = FakeSomethingRepo(dispatcher)

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
        val viewModel = CoroutineViewModel(fakeSimpleDispatcher, fakeSomethingRepo)

        val NUMBER_OF_STEPS = 10

        val asyncJob = async { viewModel.fibonacci.collectExactly(10) }
        viewModel.runFibonacci(NUMBER_OF_STEPS)
        val actual = asyncJob.await()

        assertEquals(NUMBER_OF_STEPS, actual.size)
        assertEquals(1, actual[0])
        assertEquals(1, actual[1])
        assertEquals(2, actual[2])
        assertEquals(3, actual[3])
        assertEquals(5, actual[4])
    }

    @Test
    fun setCurrentIdTest() = dispatcher.runBlockingTest {
        val viewModel = CoroutineViewModel(fakeSimpleDispatcher, fakeSomethingRepo)
        val asyncJob = async {
            viewModel.getCurrentId().take(5).toList()
        }
        viewModel.setCurrentId(6)
        viewModel.setCurrentId(7)
        viewModel.setCurrentId(8)
        viewModel.setCurrentId(9)
        viewModel.setCurrentId(10)
        val actual = asyncJob.await()
        assertEquals(listOf(6, 7, 8, 9, 10), actual)
    }
}

suspend fun <T> LiveData<T>.collectCount(count: Int): List<T> = this.asFlow().take(count).toList()

suspend fun <T> LiveData<T>.collectExactly(count: Int): List<T> {
    val result = mutableListOf<T>()

    this.asFlow().take(count).collect { result.add(it) }

    return result
}

suspend fun <T> Flow<T>.collectExactly(coroutineScope: CoroutineScope, count: Int, body: suspend () -> Unit): List<T> {
    val asyncJob = coroutineScope.async {
        this@collectExactly.take(count).toList()
    }
    body()
    return asyncJob.await()
}


class FakeCoroutineDispatchers(private val dispatcher: CoroutineDispatcher) : CoroutineDispatchers {
    override val ui: CoroutineDispatcher
        get() = dispatcher

    override val computation: CoroutineDispatcher
        get() = dispatcher
}

class FakeSomethingRepo(private val dispatcher: CoroutineDispatcher) : SomethingRepo {
    private val currentIdChannel = ConflatedBroadcastChannel<Int>()
    override val currentId: Flow<Int> = currentIdChannel.asFlow()

    override fun setCurrentId(newId: Int) {
        runBlocking(dispatcher) { currentIdChannel.send(newId) }
    }
}
