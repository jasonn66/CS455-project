package com.example.taskplanner

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import java.text.DateFormat
import java.util.*

private const val TAG = "TaskFragment"
private const val ARG_TASK_ID = "task_id"
private const val DIALOG_DATE = "DialogDate"
private const val REQUEST_DATE = 0

@Suppress("DEPRECATION")
class TaskFragment : Fragment(), DatePickerFragment.Callbacks {

    interface Callbacks {

        fun onTaskDeleted()
    }

    private var callbacks: Callbacks? = null

    private lateinit var task: Task
    private lateinit var nameField: EditText
    private lateinit var dateButton: Button
    private lateinit var completedCheckBox: CheckBox

    private val taskDetailViewModel: TaskDetailViewModel by lazy {
        ViewModelProviders.of(this).get(TaskDetailViewModel::class.java)
    }

    override fun onAttach(context: Context) {

        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        task = Task()
        val taskId: UUID = arguments?.getSerializable(ARG_TASK_ID) as UUID
        taskDetailViewModel.loadTask(taskId)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstance: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.fragment_task, container, false)
        nameField = view.findViewById(R.id.task_name) as EditText
        dateButton = view.findViewById(R.id.task_date) as Button
        completedCheckBox = view.findViewById(R.id.task_completed) as CheckBox

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        taskDetailViewModel.taskLiveData.observe(
            viewLifecycleOwner,
            Observer { task ->
                task?.let {
                    this.task = task
                    updateUI()
                }
            })
    }

    override fun onDetach() {

        super.onDetach()
        callbacks = null
    }

    override fun onStart()
    {
        super.onStart()

        val nameWatcher = object : TextWatcher
        {

            override fun beforeTextChanged(
                    sequence: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
            )
            {
                // This space intentionally left blank
            }

            override fun onTextChanged(
                    sequence: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
            )
            {
                task.name = sequence.toString()
            }

            override fun afterTextChanged(sequence: Editable?)
            {
                // This one too
            }
        }

        nameField.addTextChangedListener(nameWatcher)

        completedCheckBox.apply {
            setOnCheckedChangeListener {_, isChecked ->
                task.isCompleted = isChecked
            }
        }

        // displays the DatePickerFragment when clicked
        dateButton.setOnClickListener {
            DatePickerFragment.newInstance(task.date).apply {
                setTargetFragment(this@TaskFragment, REQUEST_DATE)
                show(this@TaskFragment.requireFragmentManager(), DIALOG_DATE)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        taskDetailViewModel.saveTask(task)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_task, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.delete_task) {
            deleteTask()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDateSelected(date: Date) {
        task.date = date
        updateUI()
    }

    private fun deleteTask() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes") { _, _ ->
            taskDetailViewModel.deleteTask(task)
            Toast.makeText(
                    requireContext(),
                    "Successfully deleted ${task.name}",
                    Toast.LENGTH_SHORT).show()
            callbacks?.onTaskDeleted()
        }
        builder.setNegativeButton("No") {_, _ -> }
        builder.setTitle("Delete ${task.name}?")
        builder.setMessage("Are you sure you want to delete this task?")
        builder.create().show()
    }

    private fun updateUI() {
        nameField.setText(task.name)
        dateButton.text = DateFormat.getDateInstance(DateFormat.MEDIUM).format(task.date)
        completedCheckBox.apply {
            isChecked = task.isCompleted
            jumpDrawablesToCurrentState()
        }
    }

    companion object {

        fun newInstance(taskId: UUID): TaskFragment {
            val args = Bundle().apply {
                putSerializable(ARG_TASK_ID, taskId)
            }
            return TaskFragment().apply {
                arguments = args
            }
        }
    }
}