package com.faridnia.mytodoapp.ui.task

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.faridnia.mytodoapp.R
import com.faridnia.mytodoapp.data.SortOrder
import com.faridnia.mytodoapp.data.Task
import com.faridnia.mytodoapp.databinding.FragmentTaskListBinding
import com.faridnia.mytodoapp.util.exhaustive
import com.faridnia.mytodoapp.util.onQueryTextChanged
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


@AndroidEntryPoint
class TaskFragment : Fragment(R.layout.fragment_task_list), TasksAdapter.OnTaskItemClicked {

    private val viewModel: TaskViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentTaskListBinding.bind(view)

        val taskAdapter = TasksAdapter(this)

        binding.apply {
            tasksRecyclerView.apply {
                adapter = taskAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }

            val touchHelper = ItemTouchHelper(object :
                ItemTouchHelper.SimpleCallback(
                    0,
                    ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                ) {

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val task = taskAdapter.currentList[viewHolder.adapterPosition]
                    viewModel.onTaskSwiped(task)
                }
            })

            touchHelper.attachToRecyclerView(tasksRecyclerView)

            addTaskFloatingActionButton.setOnClickListener {
                viewModel.onAddNewTaskClicked()
            }
        }

        setFragmentResultListener("add_edit_request") { _, bundle ->
            val result = bundle.getInt("add_edit_result")
            viewModel.onAddEditResult(result)
        }

        viewModel.tasks.observe(viewLifecycleOwner) {
            taskAdapter.submitList(it)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.taskEventFlow.collect { event ->
                when (event) {
                    is TaskViewModel.TaskEvent.ShowUndoDeleteTaskMessage -> {
                        showSnakeBar(event)
                    }
                    is TaskViewModel.TaskEvent.ShowAddTaskFragment -> {
                        val action = TaskFragmentDirections.actionTaskFragmentToAddEditTaskFragment(
                            null,
                            "New Task"
                        )
                        findNavController().navigate(action)
                    }
                    is TaskViewModel.TaskEvent.ShowEditTaskFragment -> {
                        val action = TaskFragmentDirections.actionTaskFragmentToAddEditTaskFragment(
                            event.task,
                            "Edit Task"
                        )
                        findNavController().navigate(action)
                    }
                    is TaskViewModel.TaskEvent.ShowTaskSavedConfirmationMessage -> {
                        Snackbar.make(requireView(), event.message, Snackbar.LENGTH_LONG).show()
                    }
                    is TaskViewModel.TaskEvent.ShowDeleteAllCompletedScreen -> {
                        val action = TaskFragmentDirections.actionGlobalDeleteAllCompletedTaskDialogFragment()
                        findNavController().navigate(action)
                    }
                }.exhaustive
            }
        }


        setHasOptionsMenu(true)

    }

    private fun showSnakeBar(event: TaskViewModel.TaskEvent.ShowUndoDeleteTaskMessage) {
        Snackbar.make(requireView(), "Task Deleted", Snackbar.LENGTH_LONG)
            .setAction("Undo") {
                viewModel.onUndoDeleteClicked(event.task)
            }.show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_tasks, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.onQueryTextChanged {

            viewModel.searchQuery.value = it

        }

        viewLifecycleOwner.lifecycleScope.launch {

            menu.findItem(R.id.action_hide_completed).isChecked =
                viewModel.preferencesFlow.first().hideCompleted

        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            R.id.action_sort_by_create_date -> {
                viewModel.onSortOrderSelected(SortOrder.BY_DATE)
                true
            }

            R.id.action_sort_by_name -> {
                viewModel.onSortOrderSelected(SortOrder.BY_NAME)
                true
            }

            R.id.action_hide_completed -> {
                item.isChecked = !item.isChecked
                viewModel.onHideCompletedSelected(item.isChecked)
                true
            }

            R.id.action_delete_all_completed -> {
                viewModel.onDeleteAllCompletedClicked()
                true
            }
            else -> super.onOptionsItemSelected(item)

        }
    }

    override fun onItemClick(task: Task) {
        viewModel.onTaskSelected(task)
    }

    override fun onCheckBoxClick(task: Task, isChecked: Boolean) {
        viewModel.onTaskItemCheckChanged(task, isChecked)
    }
}