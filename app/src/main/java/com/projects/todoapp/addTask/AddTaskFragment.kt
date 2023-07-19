package com.projects.todoapp.addTask

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.res.Configuration
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.projects.todoapp.R
import com.projects.todoapp.database.TodoDatabase
import com.projects.todoapp.database.model.Task
import com.projects.todoapp.databinding.FragmentAddTaskBinding
import java.util.*


class AddTaskFragment : BottomSheetDialogFragment() {
    lateinit var binding:FragmentAddTaskBinding
    var currentDate=Calendar.getInstance()
    init {
        currentDate.set(Calendar.HOUR,0)
        currentDate.set(Calendar.MINUTE,0)
        currentDate.set(Calendar.SECOND,0)
        currentDate.set(Calendar.MILLISECOND,0)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentAddTaskBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setDate()

        binding.editTextDate.setOnClickListener{
            showDatePacker()
        }

        binding.submitButton.setOnClickListener{
            addTask()
        }

    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismissListener.BottomSheetFragmentDismissed()
    }

    lateinit var onDismissListener: OnDismissListener



    fun validate():Boolean
    {
        var valid=true

        var taskTitle=binding.editTextTitle.editText?.text.toString()
        var taskDescription=binding.editTextDescription.editText?.text.toString()

        if(taskTitle.isNullOrBlank())
        {
            binding.editTextTitle.error="please enter task title"
            valid=false
        }
        else
        {
            binding.editTextTitle.error=null
        }

        if(taskDescription.isNullOrBlank())
        {
            binding.editTextDescription.error="please enter task description"
            valid=false
        }
        else
        {
            binding.editTextDescription.error=null
        }

        return valid
    }

    fun addTask()
    {
        if(validate()==false)
        {
            return
        }

        var taskTitle=binding.editTextTitle.editText?.text.toString()
        var taskDescription=binding.editTextDescription.editText?.text.toString()

        val time=Calendar.getInstance()

        TodoDatabase.getDatabase(requireActivity())?.getTaskDao()?.insertTask(Task(
            title = taskTitle,
            description = taskDescription,
            date = currentDate.timeInMillis,
            time=time.timeInMillis
        ))

        showTaskInsertedDialog()

    }

    fun showTaskInsertedDialog()
    {
        val alertDialogBuilder=AlertDialog.Builder(requireActivity())
            .setMessage("Task inserted successfully")
            .setPositiveButton(R.string.ok,{dialog,buttonId->
                dialog.dismiss()
                dismiss()
            }
            ).setCancelable(false).create()

        val activity= requireActivity()
        val currentNightMode = activity.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)
        when (currentNightMode) {
            Configuration.UI_MODE_NIGHT_NO -> {
                alertDialogBuilder.setOnShowListener {
                    alertDialogBuilder.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(activity.getColor(R.color.black))
                }
            }
            Configuration.UI_MODE_NIGHT_YES -> {
                alertDialogBuilder.setOnShowListener {
                    alertDialogBuilder.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(activity.getColor(R.color.white))
                }
            }
        }

        alertDialogBuilder.show()
    }

    fun setDate()
    {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy")
        binding.editTextDate.text= dateFormat.format(Date(currentDate.timeInMillis))
    }


    fun showDatePacker()
    {

        DatePickerDialog(requireActivity(),
            R.style.DialogTheme, DatePickerDialog.OnDateSetListener{ datePacker, year, month, day ->
                currentDate.set(Calendar.YEAR,year)
                currentDate.set(Calendar.MONTH,month)
                currentDate.set(Calendar.DAY_OF_MONTH,day)
                setDate()
            },currentDate.get(Calendar.YEAR),currentDate.get(Calendar.MONTH),currentDate.get(Calendar.DAY_OF_MONTH))
            .show()
    }

}