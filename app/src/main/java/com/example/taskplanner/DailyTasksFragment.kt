package com.example.taskplanner

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*

@Suppress("DEPRECATION")
class DailyTasksFragment: Fragment() {

    interface Callbacks {

        fun onTaskSelected(taskId: UUID)
    }

    private var callbacks: Callbacks? = null

    private lateinit var dailyTasksRecyclerView: RecyclerView
    private var adapter: TaskAdapter? = null

    private val dailyTasksViewModel: DailyTasksViewModel by lazy {

        ViewModelProviders.of(this).get(DailyTasksViewModel::class.java)
    }

    override fun onAttach(context: Context) {

        super.onAttach(context)
        callbacks = context as Callbacks?
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

    override fun onDetach() {

        super.onDetach()
        callbacks = null
    }

    private fun updateUI() {

        val tasks = dailyTasksViewModel.tasks
        adapter = TaskAdapter(tasks)
        dailyTasksRecyclerView.adapter = adapter
    }

    private inner class TaskHolder(view: View): RecyclerView.ViewHolder(view), View.OnClickListener {

        private lateinit var task: Task

        private val nameTextView: TextView = itemView.findViewById(R.id.task_name)
        private val isCompletedCheckBox: CheckBox = itemView.findViewById(R.id.task_completed)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(task: Task) {
            this.task = task
            nameTextView.text = this.task.name
            isCompletedCheckBox.isChecked = this.task.isCompleted
        }

        override fun onClick(v: View) {
            callbacks?.onTaskSelected(task.id)
        }
    }

    private inner class TaskAdapter(var tasks: List<Task>) : RecyclerView.Adapter<TaskHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskHolder {
            val view = layoutInflater.inflate(R.layout.list_item_task, parent, false)
            return TaskHolder(view)
        }

        override fun getItemCount() = tasks.size

        override fun onBindViewHolder(holder: TaskHolder, position: Int) {
            val task = tasks[position]
            holder.bind(task)
        }
    }

    companion object {
        fun newInstance(): DailyTasksFragment {
            return DailyTasksFragment()
        }
    }
}