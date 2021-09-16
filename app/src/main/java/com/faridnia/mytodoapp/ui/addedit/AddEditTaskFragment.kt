package com.faridnia.mytodoapp.ui.addedit

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.faridnia.mytodoapp.R
import com.faridnia.mytodoapp.databinding.FragmentSaveTaskBinding
import com.faridnia.mytodoapp.util.exhaustive
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class AddEditTaskFragment : Fragment(R.layout.fragment_save_task) {

    private val viewModel: AddEditTaskViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentSaveTaskBinding.bind(view)

        binding.apply {
            taskNameEditText.setText(viewModel.taskName)
            taskCheckBox.isChecked = viewModel.taskImportance
            taskCheckBox.jumpDrawablesToCurrentState()
            createDateTextView.isVisible = viewModel.task != null
            createDateTextView.text = "Created ${viewModel.task?.createDateFormatted}"

            taskNameEditText.addTextChangedListener { taskNameEditable ->
                viewModel.taskName = taskNameEditable.toString()
            }

            taskCheckBox.setOnCheckedChangeListener { _, isChecked ->
                viewModel.taskImportance = isChecked
            }

            addEditTaskFloatingActionButton.setOnClickListener {
                viewModel.onSaveClick()
            }

        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.taskEvent.collect { event ->
                when (event) {
                    is AddEditTaskViewModel.AddEditTaskEvent.NavigateBackWithResult -> {
                        binding.taskNameEditText.clearFocus()
                        setFragmentResult(
                            "add_edit_request",
                            bundleOf("add_edit_result" to event.resultCode ))
                        findNavController().popBackStack()

                    }
                    is AddEditTaskViewModel.AddEditTaskEvent.ShowInvalidInputMessage -> {
                        Snackbar.make(requireView(), event.message, Snackbar.LENGTH_LONG).show()
                    }
                }.exhaustive
            }
        }

    }

}