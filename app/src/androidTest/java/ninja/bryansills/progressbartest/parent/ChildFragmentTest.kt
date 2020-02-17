package ninja.bryansills.progressbartest.parent

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import ninja.bryansills.progressbartest.R
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ChildFragmentTest {
    lateinit var scenario: FragmentScenario<ParentFragment>

    @Before
    fun setup() {
        scenario = launchFragmentInContainer(factory = FakeFragmentFactory())
        scenario.moveToState(Lifecycle.State.RESUMED)
    }

    @Test
    fun basic() {
        onView(withId(R.id.child_text)).check(matches(withText("Child whatever")))
        onView(withId(R.id.parent_text)).check(matches(withText("Parent whatever")))
    }
}

class FakeFragmentFactory : FragmentFactory() {
    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return ParentFragment(FakeViewModelFactory())
    }
}

@Suppress("UNCHECKED_CAST")
class FakeViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel = when {
            modelClass.isAssignableFrom(ParentViewModel::class.java) -> {
                FakeParentViewModel()
            }
            modelClass.isAssignableFrom(ChildViewModel::class.java) -> {
                FakeChildViewModel()
            }
            else -> {
                throw java.lang.IllegalStateException("not good")
            }
        }

        return viewModel as T
    }
}

class FakeParentViewModel : ParentViewModel() {
    override val display = MutableLiveData<String>()

    init {
        display.value = "Parent whatever"
    }
}

class FakeChildViewModel : ChildViewModel() {
    override val childString = MutableLiveData<String>()

    init {
        childString.value = "Child whatever"
    }
}

