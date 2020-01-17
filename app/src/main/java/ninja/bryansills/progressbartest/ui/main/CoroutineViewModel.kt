package ninja.bryansills.progressbartest.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow

class CoroutineViewModel(
    private val coroutineDispatchers: CoroutineDispatchers,
    private val somethingRepo: SomethingRepo
) : ViewModel() {
    val fibonacci = MutableLiveData<Int>()

    fun runFibonacci(numberOfIterations: Int) {
        viewModelScope.launch(coroutineDispatchers.computation) {
            (1..numberOfIterations).map { step ->
                // fib(1) = 1
                // fib(2) = 1
                // fib(3) = 2
                // fib(4) = 3
                // fib(5) = 5
                // ...
                val fibValue = fib(step)

                withContext(coroutineDispatchers.ui) {
                    fibonacci.value = fibValue
                }

                delay(fibValue * 1000L)
            }
        }
    }

    fun getCurrentId(): Flow<Int> = somethingRepo.currentId
    fun setCurrentId(newId: Int) = somethingRepo.setCurrentId(newId)

    val memoizedMap: MutableMap<Int, Int> = mutableMapOf();

    private fun fib(step: Int) : Int = if (step == 0) { 0 } else { fibTail(step, 1, 0, 1) }

    private tailrec fun fibTail(max: Int, currIteration: Int, twoStep: Int, oneStep: Int): Int =
        if (currIteration >= max) {
            oneStep
        } else {
            fibTail(max, currIteration + 1, oneStep, memoizedMap.getOrPut(currIteration, {oneStep + twoStep}))
        }
}

interface CoroutineDispatchers {
    val ui : CoroutineDispatcher
    val computation : CoroutineDispatcher
}

class RealCoroutineDispatchers : CoroutineDispatchers {
    override val ui: CoroutineDispatcher
        get() = Dispatchers.Main

    override val computation: CoroutineDispatcher
        get() = Dispatchers.Default
}

interface SomethingRepo {
    val currentId: Flow<Int>
    fun setCurrentId(newId: Int)
}
