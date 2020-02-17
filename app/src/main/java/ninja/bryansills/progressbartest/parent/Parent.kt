package ninja.bryansills.progressbartest.parent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ninja.bryansills.progressbartest.databinding.FragmentParentBinding

class ParentFragment(val viewModelFactory: ViewModelProvider.Factory) : Fragment() {
    val viewModel by viewModels<ParentViewModel>(factoryProducer = {viewModelFactory})

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentParentBinding.inflate(inflater)
        childFragmentManager.beginTransaction().replace(binding.fragmentContainer.id, ChildFragment(viewModelFactory)).commit()
        return binding.root
    }
}

abstract class ParentViewModel : ViewModel() {
    abstract val display: LiveData<String>
}

class RealParentViewModel : ParentViewModel() {
    override val display = MutableLiveData<String>()
}
