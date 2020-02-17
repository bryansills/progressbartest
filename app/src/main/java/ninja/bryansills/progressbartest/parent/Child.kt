package ninja.bryansills.progressbartest.parent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import ninja.bryansills.progressbartest.databinding.FragmentChildBinding

class ChildFragment(val viewModelFactory: ViewModelProvider.Factory) : Fragment() {
    val parentViewModel by viewModels<ParentViewModel>(ownerProducer = { parentFragment as ViewModelStoreOwner }, factoryProducer = { viewModelFactory })
    val viewModel by viewModels<ChildViewModel>(factoryProducer = { viewModelFactory })

    lateinit var binding: FragmentChildBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChildBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.childString.observe(viewLifecycleOwner) { binding.childText.text = it }
        parentViewModel.display.observe(viewLifecycleOwner) { binding.parentText.text = it }
    }
}

abstract class ChildViewModel : ViewModel() {
    abstract val childString: LiveData<String>
}
