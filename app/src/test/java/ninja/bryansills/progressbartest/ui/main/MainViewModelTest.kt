package ninja.bryansills.progressbartest.ui.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MainViewModelTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    private val dispatcher = TestCoroutineDispatcher()
    private val fakeSimpleDispatcher = FakeCoroutineDispatchers(dispatcher)

    @Before
    open fun setup() {
        Dispatchers.setMain(mainThreadSurrogate)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        dispatcher.cleanupTestCoroutines()
    }

//    @Test
//    fun fail() = dispatcher.runBlockingTest {
//        Thread.setDefaultUncaughtExceptionHandler(Thread.UncaughtExceptionHandler { _, throwable ->
//            assertNotNull(throwable)
//            throw throwable
//        })
//
//        try {
//            val viewModel = MainViewModel(fakeSimpleDispatcher)
//            assertNotNull(viewModel)
//            delay(4000)
//            assertTrue(false)
//
//            viewModel.stringLiveData.observeOnce { actual ->
//                System.out.println("NOT HERE")
//                assertEquals("HAHA I SET IT ONCE", actual)
//            }
//        } catch (exception: Exception) {
//            assertNotNull(exception)
//            fail("buttz")
//        }
//
//    }

    @Test
    fun setLoading() {
        val viewModel = RealMainViewModel(fakeSimpleDispatcher, ObservableDelegate())
        viewModel.setLoading()
        viewModel.liveData.observeOnce { actual ->
            assertEquals(UiState.Loading, actual)
        }
    }

    @Test
    fun fibonacci() = dispatcher.runBlockingTest {
        val viewModel = RealMainViewModel(fakeSimpleDispatcher, ObservableDelegate())
        viewModel.fibonacci.observeOnce { actual ->
            assertNotNull(actual)
        }
        viewModel.runFibonacci(30).start()
        viewModel.fibonacci.observeOnce { actual ->
            assertNotNull(actual)
        }

        assertTrue(true)
    }

    @Test
    fun fibonacciFlow() = dispatcher.runBlockingTest {
//        async {
//            try {
//                repeat(500) { i ->
//                    println("job: I'm sleeping $i ...")
//                    delay(5000L)
//                }
//            } finally {
//                println("job: I'm running finally")
//            }
//        }

        val viewModel = RealMainViewModel(fakeSimpleDispatcher, ObservableDelegate())
        viewModel.fibonacci.observe(10) { actual ->
            assertEquals(10, actual.size)
            assertEquals(1, actual[0])
            assertEquals(1, actual[1])
            assertEquals(2, actual[2])
            assertEquals(3, actual[3])
            assertEquals(5, actual[4])
            assertEquals(8, actual[5])
        }
        async {
            viewModel.fibonacci.asFlow().take(20).collect {
                assertTrue(it < 80000)
            }
        }
        viewModel.runFibonacciFlow(10)
        viewModel.fibonacci.observeOnce { actual ->
            assertNotNull(actual)
        }

        assertTrue(true)
//        this.cleanupTestCoroutines()
//        this.cancel("because")
    }
}

fun <T> LiveData<T>.observe(count: Int, handler: (List<T>) -> Unit) {
    val observer = MultipleTimeObserver(count, handler)
    observe(observer, observer)
}

fun <T> LiveData<T>.observeOnce(onChangeHandler: (T) -> Unit) {
    this.observe(1) { onChangeHandler(it[0]) }
}

class MultipleTimeObserver<T>(private val count: Int = 1, private val handler: (List<T>) -> Unit) : Observer<T>, LifecycleOwner {
    private val lifecycle = LifecycleRegistry(this)
    private val results = mutableListOf<T>()

    init {
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    override fun getLifecycle(): Lifecycle = lifecycle

    override fun onChanged(t: T) {
        results.add(t)

        if (results.size == count) {
            handler(results)
            lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        }
    }
}
