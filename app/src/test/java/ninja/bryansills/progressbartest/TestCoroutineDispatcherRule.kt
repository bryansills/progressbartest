package ninja.bryansills.progressbartest

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.*
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class TestCoroutineDispatcherRule : TestRule {

    val dispatcher = TestCoroutineDispatcher()
    val scope = TestCoroutineScope(dispatcher)

    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            @Throws(Throwable::class)
            override fun evaluate() {

                Dispatchers.setMain(dispatcher)

                // everything above this happens before the test
                base.evaluate()
                // everything below this happens after the test

                scope.cleanupTestCoroutines()
                dispatcher.cleanupTestCoroutines()
                Dispatchers.resetMain()
            }
        }
    }

    fun runBlockingTest(block: suspend TestCoroutineScope.() -> Unit) = scope.runBlockingTest { block() }
}
