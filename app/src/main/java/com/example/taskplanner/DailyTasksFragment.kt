package com.example.taskplanner

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.DateFormat
import java.util.*

private const val TAG = "DailyTasksFragment"
private const val DIALOG_DATE = "DialogDate"
private const val REQUEST_DATE = 0

@Suppress("DEPRECATION")
class DailyTasksFragment: Fragment(), DatePickerFragment.Callbacks {

    interface Callbacks {

        fun onTaskSelected(taskId: UUID)
    }

    private var callbacks: Callbacks? = null

    private var displayDate = Date()
    private lateinit var dateButton: Button
    private lateinit var dailyTasksRecyclerView: RecyclerView
    private var adapter: TaskAdapter? = TaskAdapter(emptyList())

    private val dailyTasksViewModel: DailyTasksViewModel by lazy {

        ViewModelProviders.of(this).get(DailyTasksViewModel::class.java)
    }

    override fun onAttach(context: Context) {

        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        dailyTasksViewModel.loadTasks(displayDate)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_daily_tasks, container, false)

        dateButton = view.findViewById(R.id.daily_tasks_date) as Button
        dailyTasksRecyclerView = view.findViewById(R.id.daily_tasks_recycler_view) as RecyclerView
        dailyTasksRecyclerView.layoutManager = LinearLayoutManager(context)
        dailyTasksRecyclerView.adapter = adapter

        dateButton.text = DateFormat.getDateInstance(DateFormat.MEDIUM).format(displayDate)

        dateButton.setOnClickListener {
            DatePickerFragment.newInstance(displayDate).apply {
                setTargetFragment(this@DailyTasksFragment, REQUEST_DATE)
                show(this@DailyTasksFragment.requireFragmentManager(), DIALOG_DATE)
            }
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dailyTasksViewModel.taskListLiveData.observe(
                viewLifecycleOwner,
                Observer { tasks ->
                    tasks?.let {
                        Log.i(TAG, "Got tasks ${tasks.size}")
                        updateUI(tasks)
                    }
                }
        )
    }

    override fun onDetach() {

        super.onDetach()
        callbacks = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_daily_tasks, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.new_task -> {
                val task = Task()
                dailyTasksViewModel.addTask(task)
                callbacks?.onTaskSelected(task.id)
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onDateSelected(date: Date) {
        displayDate = date
        dateButton.text = DateFormat.getDateInstance(DateFormat.MEDIUM).format(displayDate)
        dailyTasksViewModel.loadTasks(displayDate)
    }

    private fun updateUI(tasks: List<Task>) {

        adapter = TaskAdapter(tasks)
        dailyTasksRecyclerView.adapter = adapter
        dateButton.text = DateFormat.getDateInstance(DateFormat.MEDIUM).format(displayDate)
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