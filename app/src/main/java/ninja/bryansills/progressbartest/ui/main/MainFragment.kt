package ninja.bryansills.progressbartest.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.Observable
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import ninja.bryansills.progressbartest.databinding.MainFragmentBinding

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private val viewModel: MainViewModel by viewModels { ViewModelFactory() }
    private lateinit var binding: MainFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MainFragmentBinding.inflate(inflater, container, false)
        binding.input = viewModel.input

        binding.loadingButton.setOnClickListener {
            viewModel.setLoading()
        }
        binding.loadedButton.setOnClickListener {
            viewModel.setLoaded()
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.liveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is UiState.Loaded -> {
                    binding.message.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.GONE
                }
                is UiState.Loading -> {
                    binding.message.visibility = View.GONE
                    binding.progressBar.visibility = View.VISIBLE
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        Log.d("BLARG", "BLARG")
    }
}
