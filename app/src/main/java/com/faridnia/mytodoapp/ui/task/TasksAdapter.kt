package com.faridnia.mytodoapp.ui.task

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.faridnia.mytodoapp.data.Task
import com.faridnia.mytodoapp.databinding.ItemTaskBinding


class TasksAdapter(private val listener: OnTaskItemClicked) :
    ListAdapter<Task, TasksAdapter.TasksViewHolder>(DiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TasksViewHolder {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TasksViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TasksViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    inner class TasksViewHolder(private val binding: ItemTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                root.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val task = getItem(position)
                        listener.onItemClick(task)
                    }
                }

                taskCompletedCheckBox.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val task = getItem(position)
                        listener.onCheckBoxClick(task, taskCompletedCheckBox.isChecked)
                    }
                }
            }
        }

        fun bind(task: Task) {
            binding.apply {
                taskTitleTextView.text = task.name
                taskTitleTextView.paint.isStrikeThruText = task.isCompleted
                taskCompletedCheckBox.isChecked = task.isCompleted
                taskImageView.isVisible = task.isImportant

            }
        }
    }

    interface OnTaskItemClicked {

        fun onItemClick(task: Task)

        fun onCheckBoxClick(task: Task, isChecked: Boolean)

    }

    class DiffUtilCallback : DiffUtil.ItemCallback<Task>() {

        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean =
            oldItem == newItem
    }

}