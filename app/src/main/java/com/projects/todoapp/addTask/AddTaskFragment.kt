package com.projects.todoapp.addTask

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import com.projects.todoapp.tasksList.ListFragment
import java.util.*


class AddTaskFragment : BottomSheetDialogFragment() {
    lateinit var binding:FragmentAddTaskBinding
    val temp=(ListFragment.currentDate)?.clone()
    val taskTime=Calendar.getInstance()
    var taskInserted=false
    var invalidDate=false
    var invalidTime=false
    var timeIsSelected=false

    init {
        taskTime.set(Calendar.SECOND,0)
        taskTime.set(Calendar.MILLISECOND,0)
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

        binding.taskDate.setOnClickListener{
            showDatePacker()
        }
        binding.taskTime.setOnClickListener {
            showTimePacker()
        }
        binding.submitButton.setOnClickListener{
            addTask()
        }

    }

    lateinit var onDismissListener: OnDismissListener

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (taskInserted)
        {
            onDismissListener.BottomSheetFragmentDismissed()
        }
        else
        {
            ListFragment.currentDate= temp as Calendar?
        }
    }

    var fieldsAreEmpty=false

    fun validate():Boolean
    {
        var valid=true

        var taskTitle=binding.editTextTitle.editText?.text.toString()
        var taskDescription=binding.editTextDescription.editText?.text.toString()

        if(taskTitle.isNullOrBlank())
        {
            binding.editTextTitle.error="please enter task title"
            fieldsAreEmpty=true
            valid=false
        }
        else
        {
            binding.editTextTitle.error=null
            fieldsAreEmpty=false
        }

        if(taskDescription.isNullOrBlank())
        {
            binding.editTextDescription.error="please enter task description"
            fieldsAreEmpty=true
            valid=false
        }
        else
        {
            binding.editTextDescription.error=null
            fieldsAreEmpty=false
        }

        val toDay=Calendar.getInstance()
        toDay.set(Calendar.HOUR,0)
        toDay.set(Calendar.MINUTE,0)
        toDay.set(Calendar.SECOND,0)
        toDay.set(Calendar.MILLISECOND,0)

        if(ListFragment.currentDate?.timeInMillis!! < toDay.timeInMillis)
        {
            valid=false
            invalidDate=true
        }

        val timeNow=Calendar.getInstance()
        timeNow.set(Calendar.SECOND,0)
        timeNow.set(Calendar.MILLISECOND,0)

        if (binding.taskTime.text.isEmpty()||(ListFragment.currentDate?.timeInMillis!! == toDay.timeInMillis && taskTime.timeInMillis<timeNow.timeInMillis))
        {
            valid=false
            invalidTime=true
        }

        return valid
    }

    fun addTask()
    {

        if(!validate())
        {
            if (!fieldsAreEmpty)
            {
                if(invalidDate)
                {
                    showTaskInsertedDialog("Task is not inserted, the date you choose is invalid, you can view it's tasks only")
                }
                else if (invalidTime)
                {
                    if(timeIsSelected)
                    {
                        showTaskInsertedDialog("Task is not inserted, the time you choose is invalid")
                    }
                    else
                    {
                        showTaskInsertedDialog("Task is not inserted, you should select time")
                    }
                }
            }
            return
        }

        var taskTitle=binding.editTextTitle.editText?.text.toString()
        var taskDescription=binding.editTextDescription.editText?.text.toString()

        TodoDatabase.getDatabase(requireActivity())?.getTaskDao()?.insertTask(Task(
            title = taskTitle,
            description = taskDescription,
            date = ListFragment.currentDate?.timeInMillis,
            time=taskTime.timeInMillis
        ))

        taskInserted=true
        showTaskInsertedDialog("Task inserted successfully")
    }

    fun showTaskInsertedDialog(message: String)
    {
        val alertDialogBuilder=AlertDialog.Builder(requireActivity())
            .setMessage(message)
            .setPositiveButton(R.string.ok,{dialog,buttonId->
                if (invalidDate==false&&invalidTime==false)
                {
                    dialog.dismiss()
                    dismiss()
                }
                invalidDate=false
                invalidTime=false
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
        binding.taskDate.text= dateFormat.format(Date(ListFragment.currentDate?.timeInMillis!!))
    }

    fun setTime()
    {
        val dateFormat = SimpleDateFormat("h:mm aa")

        binding.taskTime.text= dateFormat.format(Date(taskTime.timeInMillis!!))
    }

    private fun showDatePacker()
    {
        DatePickerDialog(requireActivity(),
            R.style.DialogTheme, DatePickerDialog.OnDateSetListener{ datePacker, year, month, day ->
                ListFragment.currentDate?.set(Calendar.YEAR,year)
                ListFragment.currentDate?.set(Calendar.MONTH,month)
                ListFragment.currentDate?.set(Calendar.DAY_OF_MONTH,day)
                setDate()
            },ListFragment.currentDate?.get(Calendar.YEAR)!!,ListFragment.currentDate?.get(Calendar.MONTH)!!,ListFragment.currentDate?.get(Calendar.DAY_OF_MONTH)!!)
            .show()
    }

    private fun showTimePacker()
    {
        TimePickerDialog(requireActivity(),
            R.style.DialogTheme,TimePickerDialog.OnTimeSetListener{ timePacker,hour,minute ->
            taskTime.set(Calendar.HOUR_OF_DAY,hour)
            taskTime.set(Calendar.MINUTE,minute)
            setTime()
            timeIsSelected=true
        },taskTime.get(Calendar.HOUR_OF_DAY),taskTime.get(Calendar.MINUTE),false).show()
    }

}