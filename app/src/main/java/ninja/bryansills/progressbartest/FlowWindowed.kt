package ninja.bryansills.progressbartest

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.collect
import kotlin.math.max
import kotlin.math.min

@FlowPreview
public fun <T> Flow<T>.windowed(size: Int, step: Int, partialWindows: Boolean): Flow<List<T>> =
    windowed(size, step, partialWindows) { it.toList() }

@OptIn(ExperimentalStdlibApi::class)
@FlowPreview
public fun <T, R> Flow<T>.windowed(size: Int, step: Int, partialWindows: Boolean, transform: suspend (List<T>) -> R): Flow<R> {
    require(size > 0 && step > 0) { "Size and step should be greater than 0, but was size: $size, step: $step" }

    return flow {
        val buffer = ArrayDeque<T>(size)
        val toDrop = min(step, size)
        val toSkip = max(step - size, 0)
        var skipped = toSkip

        collect { value ->
            if (toSkip == skipped) buffer.addLast(value)
            else skipped++

            if (buffer.size == size) {
                emit(transform(buffer))
                repeat(toDrop) { buffer.removeFirst() }
                skipped = 0
            }
        }

        while (partialWindows && buffer.isNotEmpty()) {
            emit(transform(buffer))
            repeat(min(toDrop, buffer.size)) { buffer.removeFirst() }
        }
    }
}
