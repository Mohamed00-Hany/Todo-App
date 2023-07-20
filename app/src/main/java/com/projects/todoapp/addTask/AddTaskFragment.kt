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
import com.projects.todoapp.tasksList.CurrentDate
import java.util.*


class AddTaskFragment : BottomSheetDialogFragment() {
    lateinit var binding:FragmentAddTaskBinding
    var currentDate=Calendar.getInstance()
    var dateIsChanged=false
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

        setDate(false)

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
        if (dateIsChanged)
        {
            CurrentDate.currentDate=currentDate
            dateIsChanged=false
        }
        TodoDatabase.getDatabase(requireActivity())?.getTaskDao()?.insertTask(Task(
            title = taskTitle,
            description = taskDescription,
            date = CurrentDate.currentDate?.timeInMillis,
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

    fun setDate(stateOfDate:Boolean)
    {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy")

        if (stateOfDate)
        {
            binding.editTextDate.text= dateFormat.format(Date(currentDate.timeInMillis!!))
        }
        else
        {
            binding.editTextDate.text= dateFormat.format(Date(CurrentDate.currentDate?.timeInMillis!!))
        }
    }

    private fun showDatePacker()
    {
        DatePickerDialog(requireActivity(),
            R.style.DialogTheme, DatePickerDialog.OnDateSetListener{ datePacker, year, month, day ->
                currentDate.set(Calendar.YEAR,year)
                currentDate.set(Calendar.MONTH,month)
                currentDate.set(Calendar.DAY_OF_MONTH,day)
                setDate(true)
                dateIsChanged=true
            },CurrentDate.currentDate?.get(Calendar.YEAR)!!,CurrentDate.currentDate?.get(Calendar.MONTH)!!,CurrentDate.currentDate?.get(Calendar.DAY_OF_MONTH)!!)
            .show()
    }

}