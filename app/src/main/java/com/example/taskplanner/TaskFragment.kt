package com.example.taskplanner

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.Observer
import java.util.*

private const val TAG = "TaskFragment"
private const val ARG_TASK_ID = "task_id"
private const val DIALOG_DATE = "DialogDate"

@Suppress("DEPRECATION")
class TaskFragment : Fragment() {

    private lateinit var task: Task
    private lateinit var nameField: EditText
    private lateinit var dateButton: Button
    private lateinit var completedCheckBox: CheckBox

    private val taskDetailViewModel: TaskDetailViewModel by lazy {
        ViewModelProviders.of(this).get(TaskDetailViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
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

        dateButton.setOnClickListener {
            DatePickerFragment().apply {
                show(this@TaskFragment.requireFragmentManager(), DIALOG_DATE)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        taskDetailViewModel.saveTask(task)
    }

    private fun updateUI() {
        nameField.setText(task.name)
        dateButton.text = task.date.toString()
        completedCheckBox.isChecked = task.isCompleted
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