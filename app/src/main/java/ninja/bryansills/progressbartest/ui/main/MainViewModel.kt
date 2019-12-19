package ninja.bryansills.progressbartest.ui.main

import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import ninja.bryansills.progressbartest.BR

class RealMainViewModel(val coroutineDispatchers: CoroutineDispatchers, val observableDelegate: ObservableDelegate) : MainViewModel(), Observable by observableDelegate {
    override val liveData = MutableLiveData<UiState>()
    var stringLiveData = MutableLiveData<String>()
    val fibonacci = MutableLiveData<Int>()

    init {
        viewModelScope.launch(coroutineDispatchers.ui) {
            delay(1000)
//            stringLiveData.postValue(throwNpe())

//            try {
//                delay(5000)
//            } catch (ex: Exception) {
////                stringLiveData.value = "HAHA I SET IT WRONG"
//                throw ex
//            }
        }
    }

    override var input: String = ""
        @Bindable get() = field
        set(value) {
            field = value
            observableDelegate.notifyPropertyChanged(BR._all)
        }

    private fun throwNpe(): String {
        var bad: String? = null
        return bad!!
    }

    fun runFibonacci(numberOfIterations: Int): Job {
        return viewModelScope.launch(coroutineDispatchers.computation) {
            (1..numberOfIterations).map { step ->
                val fibValue = fib(step)

                withContext(coroutineDispatchers.ui) {
                    withContext(coroutineDispatchers.ui) {
                        fibonacci.value = fibValue
                    }
                }

                delay(fibValue * 1000L)
            }
        }
    }

    @UseExperimental(InternalCoroutinesApi::class)
    fun runFibonacciFlow(numberOfIterations: Int) {
        viewModelScope.launch(coroutineDispatchers.computation) {
            fibFlow(numberOfIterations).collect { value ->
                withContext(coroutineDispatchers.ui) {
                    fibonacci.value = value
                }
                delay(2000L)
            }
        }
    }

    private fun fibFlow(numberOfIterations: Int): Flow<Int> = flow {
        for (step in 1..numberOfIterations) {
            val result = fib(step)
            delay(result * 1000L)
            emit(result)
        }
    }

    override fun setLoading() {
        liveData.value = UiState.Loading
    }

    override fun setLoaded() {
        liveData.value = UiState.Loaded
    }

    val memoizedMap: MutableMap<Int, Int> = mutableMapOf();

    private fun fib(step: Int) : Int = if (step == 0) { 0 } else { fibTail(step, 1, 0, 1) }

    private tailrec fun fibTail(max: Int, currIteration: Int, twoStep: Int, oneStep: Int): Int =
        if (currIteration >= max) {
            oneStep
        } else {
            fibTail(max, currIteration + 1, oneStep, memoizedMap.getOrPut(currIteration, {oneStep + twoStep}))
        }
}

abstract class MainViewModel : ViewModel() {
    abstract var input: String
    abstract val liveData: LiveData<UiState>
    abstract fun setLoading()
    abstract fun setLoaded()
}

sealed class UiState {
    object Loading : UiState()
    object Loaded : UiState()
}
