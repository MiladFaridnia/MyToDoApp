package com.faridnia.mytodoapp.ui.task

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.faridnia.mytodoapp.R
import com.faridnia.mytodoapp.databinding.FragmentTaskListBinding
import com.faridnia.mytodoapp.util.onQueryTextChanged
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class TaskFragment : Fragment(R.layout.fragment_task_list) {

    private val viewModel: TaskViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentTaskListBinding.bind(view)

        val taskAdapter = TasksAdapter()

        binding.tasksRecyclerView.apply {
            adapter = taskAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }

        viewModel.tasks.observe(viewLifecycleOwner) {
            taskAdapter.submitList(it)
        }

        setHasOptionsMenu(true)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_tasks, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.onQueryTextChanged {
            viewModel.searchQuery.value = it
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sort_by_create_date -> {
                viewModel.sortOrder.value = TaskViewModel.SortOrder.BY_DATE
                true
            }
            R.id.action_sort_by_name -> {
                viewModel.sortOrder.value = TaskViewModel.SortOrder.BY_NAME
                true
            }
            R.id.action_hide_completed -> {
                item.isChecked = !item.isChecked
                viewModel.hideCompleted.value = item.isChecked
                true
            }
            R.id.action_delete_all_completed -> {
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}