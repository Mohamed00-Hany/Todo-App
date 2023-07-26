package com.projects.todoapp.ui.tasksList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.github.hachimann.materialcalendarview.CalendarDay
import com.projects.todoapp.database.TodoDatabase
import com.projects.todoapp.database.model.Task
import com.projects.todoapp.databinding.FragmentListBinding
import com.projects.todoapp.ui.taskDescription.TaskDescriptionFragment
import kotlinx.coroutines.launch
import java.util.Calendar

class ListFragment : Fragment(){
    lateinit var binding:FragmentListBinding
    lateinit var recyclerView: RecyclerView
    lateinit var tasksAdapter: TasksAdapter
    var timeOfNewInsertedTask:Long?=null
    companion object
    {
        var currentDate:Calendar?= Calendar.getInstance()
        var tasksList:List<Task>?=null
    }

    init {
        currentDate?.set(Calendar.HOUR,0)
        currentDate?.set(Calendar.MINUTE,0)
        currentDate?.set(Calendar.SECOND,0)
        currentDate?.set(Calendar.MILLISECOND,0)
        currentDate?.set(Calendar.AM_PM,0)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentListBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView=binding.recyclerViewTasks
        tasksAdapter= TasksAdapter(null)
        recyclerView.adapter=tasksAdapter
        registerTaskCallBacks()
        if (savedInstanceState==null)
        {
            binding.calendarView.selectedDate= CalendarDay.today()
        }
        if (tasksList!=null)
        {
            tasksAdapter.changeData(tasksList)
        }
    }

    private fun registerTaskCallBacks()
    {
        binding.calendarView.setOnDateChangedListener{ calender,selectedDate,selected ->
            if (selected)
            {
                currentDate?.set(Calendar.YEAR,selectedDate.year)
                currentDate?.set(Calendar.MONTH,selectedDate.month-1)
                currentDate?.set(Calendar.DAY_OF_MONTH,selectedDate.day)
                tasksList=null
                lifecycleScope.launch {
                    loadData()
                    recyclerView.scrollToPosition(0)
                }
            }
        }

        tasksAdapter.onTaskClickListener=object : TasksAdapter.OnTaskClickListener
        {
            override fun showTaskDescriptionFragment(position : Int) {
                val taskDescriptionFragment = TaskDescriptionFragment()
                taskDescriptionFragment.show(childFragmentManager,"")
                val description=tasksList?.get(position)?.description
                taskDescriptionFragment.setTaskDescription(description)
            }
        }

        tasksAdapter.onDeleteClickListener=object : TasksAdapter.OnDeleteClickListener
        {
            override fun deleteTask(position: Int) {
                lifecycleScope.launch {
                    TodoDatabase.getDatabase(requireActivity())?.getTaskDao()?.deleteTask(tasksList?.get(position)?:Task())
                    tasksList=null
                    loadData()
                }
            }
        }

        tasksAdapter.onDoneClickListener=object : TasksAdapter.OnDoneClickListener
        {
            override fun taskDone(position: Int) {
                val updatedTask=tasksList?.get(position)
                updatedTask?.isDone=true
                lifecycleScope.launch {
                    TodoDatabase.getDatabase(requireActivity())?.getTaskDao()?.updateTask(updatedTask?:Task())
                    tasksList=null
                    loadData()
                }
            }
        }
    }

    var onResumedListener: OnFragmentStarted?=null

    interface OnFragmentStarted
    {
        fun bindActivityTitle()
    }

    override fun onStart() {
        super.onStart()
        onResumedListener?.bindActivityTitle()
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            loadData()
            timeOfNewInsertedTask?.let {
                scrollToAddedTask(timeOfNewInsertedTask!!)
                timeOfNewInsertedTask=null
            }
        }
    }


    suspend fun loadData()
    {
        if(isResumed&&tasksList==null)
        {
            val calendarDay=CalendarDay.from(
                currentDate?.get(Calendar.YEAR)!!
                , currentDate?.get(Calendar.MONTH)!! +1
                , currentDate?.get(Calendar.DAY_OF_MONTH)!!)

            if (calendarDay.date!=binding.calendarView.selectedDate?.date)
            {
                binding.calendarView.currentDate= calendarDay
                binding.calendarView.selectedDate= calendarDay
            }

            tasksList=TodoDatabase.getDatabase(requireActivity())?.getTaskDao()?.getTasksByDate(
                currentDate?.timeInMillis!!)
            tasksAdapter.changeData(tasksList)
        }
        binding.welcomeText.isVisible = tasksList?.isEmpty() == true
    }

    var onFragmentDestroyedListener: OnFragmentDestroyed?=null

    interface OnFragmentDestroyed
    {
        fun onDestroyed()
    }

    override fun onDestroy() {
        super.onDestroy()
        onFragmentDestroyedListener?.onDestroyed()
    }

    fun scrollToAddedTask(taskTime:Long) {
        if (isResumed)
        {
            var taskPosition=-1
            for (i in 0 until tasksList?.size!!)
            {
                if(tasksList!![i].time==taskTime)
                {
                    taskPosition=i
                }
            }
            if (taskPosition!=-1)
            {
                recyclerView.smoothScrollToPosition(taskPosition)
            }
        }
    }

}