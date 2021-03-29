package com.example.taskplanner

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.fragment.app.Fragment

class TaskFragment : Fragment() {

    private lateinit var task: Task
    private lateinit var nameField: EditText
    private lateinit var dateButton: Button
    private lateinit var completedCheckBox: CheckBox

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        task = Task()
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

        dateButton.apply{
            text = task.date.toString()
            isEnabled = false
        }

        return view
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
    }
}