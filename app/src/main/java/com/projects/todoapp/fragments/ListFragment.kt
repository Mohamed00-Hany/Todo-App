package com.projects.todoapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.github.hachimann.materialcalendarview.CalendarDay
import com.projects.todoapp.TasksAdapter
import com.projects.todoapp.database.TodoDatabase
import com.projects.todoapp.database.model.Task
import com.projects.todoapp.databinding.FragmentListBinding
import java.util.Calendar

class ListFragment : Fragment(){
    lateinit var binding:FragmentListBinding
    lateinit var recyclerView: RecyclerView
    lateinit var tasksAdapter: TasksAdapter

    var tasksList:List<Task>?=null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentListBinding.inflate(inflater,container,false)
        //TodoDatabase.getDatabase(requireActivity())?.getTaskDao()?.deleteAllTasks()
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView=binding.recyclerViewTasks
        tasksAdapter=TasksAdapter(null)
        recyclerView.adapter=tasksAdapter

        tasksAdapter.onTaskClickListener=object : TasksAdapter.OnTaskClickListener
        {
            override fun showTaskDescriptionFragment(position : Int) {
               val taskDescriptionFragment = TaskDescriptionFragment()
                taskDescriptionFragment.show(childFragmentManager,"")
                val description=tasksList?.get(position)?.description
                taskDescriptionFragment.setTaskDescription(description)
            }
        }

        binding.calendarView.setOnDateChangedListener{ calender,selectedDate,selected ->
            if (selected)
            {
                currentDate.set(Calendar.YEAR,selectedDate.year)
                currentDate.set(Calendar.MONTH,selectedDate.month-1)
                currentDate.set(Calendar.DAY_OF_MONTH,selectedDate.day)
                loadData()
            }

        }
        binding.calendarView.selectedDate= CalendarDay.today()


        tasksAdapter.onDeleteClickListener=object : TasksAdapter.OnDeleteClickListener
        {
            override fun deleteTask(position: Int) {
                TodoDatabase.getDatabase(requireActivity())?.getTaskDao()?.deleteTask(tasksList?.get(position)?:Task())
                loadData()
            }

        }

        tasksAdapter.onDoneClickListener=object : TasksAdapter.OnDoneClickListener
        {
            override fun taskDone(position: Int) {
                val updatedTask=tasksList?.get(position)
                updatedTask?.isDone=true
                TodoDatabase.getDatabase(requireActivity())?.getTaskDao()?.updateTask(updatedTask?:Task())
                loadData()
            }

        }

    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    val currentDate=Calendar.getInstance()
    init {
        currentDate.set(Calendar.HOUR,0)
        currentDate.set(Calendar.MINUTE,0)
        currentDate.set(Calendar.SECOND,0)
        currentDate.set(Calendar.MILLISECOND,0)
    }

    fun loadData()
    {
        if(isResumed)
        {
            tasksList=TodoDatabase.getDatabase(requireActivity())?.getTaskDao()?.getTasksByDate(currentDate.timeInMillis)
            tasksAdapter.changeData(tasksList)
        }
        else
        {
            return
        }

    }

}