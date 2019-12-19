package ninja.bryansills.progressbartest.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            RealMainViewModel(RealCoroutineDispatchers(), ObservableDelegate()) as T
        } else {
            throw IllegalStateException("Gotta define a ViewModel, yo.")
        }
    }
}
