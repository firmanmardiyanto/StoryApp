package org.firmanmardiyanto.storyapp.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.firmanmardiyanto.core.ui.LoadingStateAdapter
import org.firmanmardiyanto.core.ui.StoriesAdapter
import org.firmanmardiyanto.storyapp.databinding.FragmentHomeBinding
import org.firmanmardiyanto.storyapp.detail.DetailStoryActivity
import org.firmanmardiyanto.storyapp.poststory.PostStoryActivity
import org.koin.android.viewmodel.ext.android.viewModel

class HomeFragment : Fragment() {
    private val homeViewModel: HomeViewModel by viewModel()
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: StoriesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                adapter.refresh()
                binding.rvStories.scrollToPosition(0)
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (activity != null) {

            binding.fab.setOnClickListener {
                resultLauncher.launch(Intent(activity, PostStoryActivity::class.java))
            }
            postponeEnterTransition()
            adapter = StoriesAdapter()
            adapter.onItemClick = { selectedStory, selectedItemListStoryBinding ->
                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        requireActivity(),
                        Pair(selectedItemListStoryBinding.ivItemImage, "item_image"),
                        Pair(selectedItemListStoryBinding.tvItemName, "item_name"),
                        Pair(selectedItemListStoryBinding.tvItemDescription, "item_description")
                    )
                val intent = Intent(requireContext(), DetailStoryActivity::class.java)
                intent.putExtra(DetailStoryActivity.EXTRA_DATA, selectedStory)
                startActivity(intent, optionsCompat.toBundle())
            }
            lifecycleScope.launch {
                adapter.loadStateFlow.collectLatest {
                    with(binding) {
                        refresh.isRefreshing = it.refresh is LoadState.Loading
                        viewError.root.visibility =
                            if (it.refresh is LoadState.Error) View.VISIBLE else View.GONE
                    }
                }
            }
            binding.refresh.setOnRefreshListener {
                adapter.refresh()
                binding.rvStories.scrollToPosition(0)
            }
            binding.rvStories.layoutManager = LinearLayoutManager(requireContext())
            binding.rvStories.adapter = adapter.withLoadStateFooter(
                footer = LoadingStateAdapter {
                    adapter.retry()
                }
            )
            homeViewModel.storiesPaging.observe(viewLifecycleOwner) {
                adapter.submitData(lifecycle, it)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}