package com.example.taskplanner

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

@Suppress("DEPRECATION")
class DailyTasksFragment: Fragment() {

    private lateinit var dailyTasksRecyclerView: RecyclerView
    private var adapter: TaskAdapter? = null

    private val dailyTasksViewModel: DailyTasksViewModel by lazy {

        ViewModelProviders.of(this).get(DailyTasksViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        Log.d(TAG, "Total tasks: ${dailyTasksViewModel.tasks.size}")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_daily_tasks, container, false)

        dailyTasksRecyclerView = view.findViewById(R.id.daily_tasks_recycler_view) as RecyclerView
        dailyTasksRecyclerView.layoutManager = LinearLayoutManager(context)

        updateUI()

        return view
    }

    private fun updateUI() {

        val tasks = dailyTasksViewModel.tasks
        adapter = TaskAdapter(tasks)
        dailyTasksRecyclerView.adapter = adapter
    }

    private inner class TaskHolder(view: View): RecyclerView.ViewHolder(view) {

        val nameTextView: TextView = itemView.findViewById(R.id.task_name)
        val isCompletedCheckBox: CheckBox = itemView.findViewById(R.id.task_completed)
    }

    private inner class TaskAdapter(var tasks: List<Task>) : RecyclerView.Adapter<TaskHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskHolder {
            val view = layoutInflater.inflate(R.layout.list_item_task, parent, false)
            return TaskHolder(view)
        }

        override fun getItemCount() = tasks.size

        override fun onBindViewHolder(holder: TaskHolder, position: Int) {
            val task = tasks[position]
            holder.apply {
                nameTextView.text = task.name
                isCompletedCheckBox.isChecked = task.isCompleted
            }
        }
    }

    companion object {
        fun newInstance(): DailyTasksFragment {
            return DailyTasksFragment()
        }
    }
}