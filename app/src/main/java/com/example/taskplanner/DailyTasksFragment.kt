package com.example.taskplanner

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
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
import org.w3c.dom.Text
import java.text.DateFormat
import java.util.*
import kotlin.math.abs

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
    private lateinit var dateTextView: TextView
    private lateinit var dailyTasksRecyclerView: RecyclerView
    private lateinit var noTasksTextView: TextView
    private lateinit var addTaskButton: Button
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
        displayDate = setDateTimeMidnight(displayDate)
        dailyTasksViewModel.loadTasks(displayDate)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_daily_tasks, container, false)

        dateTextView = view.findViewById(R.id.daily_tasks_date) as TextView
        dailyTasksRecyclerView = view.findViewById(R.id.daily_tasks_recycler_view) as RecyclerView
        dailyTasksRecyclerView.layoutManager = LinearLayoutManager(context)
        dailyTasksRecyclerView.adapter = adapter
        noTasksTextView = view.findViewById(R.id.no_tasks) as TextView
        addTaskButton = view.findViewById(R.id.add_task) as Button

        view.setOnTouchListener(OnSwipeTouchListener())

        dateTextView.text = DateFormat.getDateInstance(DateFormat.MEDIUM).format(displayDate)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observes any changes to the list of tasks and updates the UI
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

    override fun onStart() {
        super.onStart()

        addTaskButton.setOnClickListener {
            addTask()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_daily_tasks, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==R.id.new_task) {
            addTask()
        }
        else if(item.itemId==R.id.select_date) {
            // Displays the DatePicker when the select date menu item is selected
            DatePickerFragment.newInstance(displayDate).apply {
                setTargetFragment(this@DailyTasksFragment, REQUEST_DATE)
                show(this@DailyTasksFragment.requireFragmentManager(), DIALOG_DATE)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    // Receives the selected date from the DatePicker and loads tasks for the new date
    override fun onDateSelected(date: Date) {
        displayDate = date
        dateTextView.text = DateFormat.getDateInstance(DateFormat.MEDIUM).format(displayDate)
        dailyTasksViewModel.loadTasks(displayDate)
    }

    // Adds a new blank Task to the database and views the blank task in the Task Fragment
    private fun addTask() {
        val task = Task()
        task.date = setDateTimeMidnight(task.date)
        dailyTasksViewModel.addTask(task)
        callbacks?.onTaskSelected(task.id)
    }

    // Sets the time value in the given date to midnight
    private fun setDateTimeMidnight(date: Date) : Date {

        val calendar = Calendar.getInstance()
        calendar.time = date
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        return (GregorianCalendar(year, month, day).time)
    }

    private fun updateUI(tasks: List<Task>) {

        // If there are no tasks in the RecyclerView, display TextView and Button, otherwise hide them
        if(tasks.isEmpty()) {
            noTasksTextView.visibility = VISIBLE
            addTaskButton.visibility = VISIBLE
        }
        else {
            noTasksTextView.visibility = GONE
            addTaskButton.visibility = GONE
        }
        adapter = TaskAdapter(tasks)
        dailyTasksRecyclerView.adapter = adapter
        dateTextView.text = DateFormat.getDateInstance(DateFormat.MEDIUM).format(displayDate)
    }

    // Wraps Item View for the RecyclerView
    private inner class TaskHolder(view: View): RecyclerView.ViewHolder(view), View.OnClickListener {

        private lateinit var task: Task

        private val nameTextView: TextView = itemView.findViewById(R.id.task_name)
        private val isCompletedCheckBox: CheckBox = itemView.findViewById(R.id.task_completed)

        init {
            itemView.setOnClickListener(this)
        }

        // Binds data from the model layer
        fun bind(task: Task) {
            this.task = task
            nameTextView.text = this.task.name
            isCompletedCheckBox.isChecked = this.task.isCompleted

            // Listener for the checkbox that updates the value in the database
            isCompletedCheckBox.setOnCheckedChangeListener { _, isChecked ->
                this.task.isCompleted = isChecked
                dailyTasksViewModel.saveTask(this.task)
            }
        }

        override fun onClick(v: View) {
            callbacks?.onTaskSelected(task.id)
        }
    }

    // Creates TaskHolders when asked and binds TaskHolders to data from the model layer
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

    // OnSwipeTouchListener changes the display date when the user swipes left or right
    private inner class OnSwipeTouchListener : View.OnTouchListener {

        private var gestureDetector: GestureDetector

        init {
            gestureDetector = GestureDetector(context, GestureListener())
        }

        fun onSwipeLeft() {
            // Increment the display date
            val calendar = Calendar.getInstance()
            calendar.time = displayDate
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            displayDate = GregorianCalendar(year, month, day).time

            dailyTasksViewModel.loadTasks(displayDate)
        }

        fun onSwipeRight() {
            // Decrement the display date
            val calendar = Calendar.getInstance()
            calendar.time = displayDate
            calendar.add(Calendar.DAY_OF_MONTH, -1)
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            displayDate = GregorianCalendar(year, month, day).time

            dailyTasksViewModel.loadTasks(displayDate)
        }

        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            return gestureDetector.onTouchEvent(event)
        }

        // GestureListener detects when the user swipes left or right
        private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {

            private val swipeDistanceThreshold = 100
            private val swipeVelocityThreshold = 100

            override fun onDown(e: MotionEvent?): Boolean {
                return true
            }

            override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
                val distanceX = e2.x - e1.x
                val distanceY = e2.y - e1.y

                if(abs(distanceX) > abs(distanceY) &&
                        abs(distanceX) > swipeDistanceThreshold &&
                        abs(velocityX) > swipeVelocityThreshold) {
                    if(distanceX > 0) {
                        onSwipeRight()
                    }
                    else {
                        onSwipeLeft()
                    }

                    return true
                }

                return false
            }
        }
    }

    companion object {
        fun newInstance(): DailyTasksFragment {
            return DailyTasksFragment()
        }
    }
}