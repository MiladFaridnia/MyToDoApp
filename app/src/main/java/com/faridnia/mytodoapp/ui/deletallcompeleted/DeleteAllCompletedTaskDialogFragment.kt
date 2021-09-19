package com.faridnia.mytodoapp.ui.deletallcompeleted

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeleteAllCompletedTaskDialogFragment : DialogFragment() {

    private val viewModel: DeleteAllCompletedTaskViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle("Delete All Completed Tasks")
            .setMessage("Do you want to delete all completed tasks?")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Yes")
            { _, _ ->
                viewModel.onConfirmDialog()
            }
            .create()
    }
}