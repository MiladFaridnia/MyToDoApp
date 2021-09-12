package com.faridnia.mytodoapp.ui.addedit

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.faridnia.mytodoapp.R
import com.faridnia.mytodoapp.databinding.FragmentSaveTaskBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddEditTaskFragment : Fragment(R.layout.fragment_save_task) {

    private val viewModel : AddEditTaskViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentSaveTaskBinding.bind(view)

        binding.apply {
            taskNameEditText.setText(viewModel.taskName)
            taskCheckBox.isChecked = viewModel.taskImportance
            taskCheckBox.jumpDrawablesToCurrentState()
            createDateTextView.isVisible = viewModel.task != null
            createDateTextView.text = "Created ${viewModel.task?.createDateFormatted}"
        }

    }

}