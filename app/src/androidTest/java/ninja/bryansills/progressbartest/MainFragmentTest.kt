package ninja.bryansills.progressbartest

import android.service.autofill.Validators.not
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.ext.junit.runners.AndroidJUnit4
import ninja.bryansills.progressbartest.ui.main.MainFragment
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.Timeout
import org.junit.runner.RunWith
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.ProgressBar
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.actionWithAssertions
import androidx.test.espresso.matcher.ViewMatchers.*
import org.hamcrest.Matcher
import androidx.test.espresso.Espresso.onView




@RunWith(AndroidJUnit4::class)
class MainFragmentTest {

    @get:Rule
    val timeout: Timeout = Timeout.seconds(3)

    private lateinit var scenario: FragmentScenario<MainFragment>

    @Before
    fun setup() {
        scenario = launchFragmentInContainer(themeResId = R.style.AppTheme)
        scenario.moveToState(Lifecycle.State.RESUMED)
        onView(isAssignableFrom(ProgressBar::class.java)).perform(replaceProgressBarDrawable())
    }

    @After
    fun teardown() {
        scenario.recreate()
    }

    @Test
    fun basic() {
        onView(withId(R.id.message)).check(matches(not(isDisplayed())))
        onView(withId(R.id.progressBar)).check(matches(not(isDisplayed())))
    }

    @Test
    fun loaded() {
        onView(withId(R.id.loadedButton)).perform(click())
        onView(withId(R.id.message)).check(matches(isDisplayed()))
        onView(withId(R.id.progressBar)).check(matches(not(isDisplayed())))
    }

    @Test
    fun loading() {
        onView(withId(R.id.loadingButton)).perform(click())
        onView(withId(R.id.message)).check(matches(not(isDisplayed())))
        onView(withId(R.id.progressBar)).check(matches(isDisplayed()))
    }
}

fun replaceProgressBarDrawable(): ViewAction {
    return actionWithAssertions(object : ViewAction {
        override fun getConstraints(): Matcher<View> {
            return isAssignableFrom(ProgressBar::class.java)
        }

        override fun getDescription(): String {
            return "replace the ProgressBar drawable"
        }

        override fun perform(uiController: UiController, view: View) {
            // Replace the indeterminate drawable with a static red ColorDrawable
            val progressBar = view as ProgressBar
            progressBar.indeterminateDrawable = ColorDrawable(-0x10000)
            uiController.loopMainThreadUntilIdle()
        }
    })
}
