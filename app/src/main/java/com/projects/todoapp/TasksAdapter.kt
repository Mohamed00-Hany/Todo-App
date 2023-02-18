package com.projects.todoapp

import android.content.Context
import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.projects.todoapp.database.TodoDatabase
import com.projects.todoapp.database.model.Task
import com.projects.todoapp.databinding.ItemTaskBinding
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class TasksAdapter(private var tasksList:List<Task>?) : RecyclerView.Adapter<TasksAdapter.ViewHolder>()
{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewBinding=ItemTaskBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(viewBinding)
    }

    override fun getItemCount(): Int = tasksList?.size ?: 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item =tasksList?.get(position)
        holder.itemBinding.taskTitle.text= item?.title
        holder.itemBinding.taskTime.text= " " + getTime(item?.time?:0)
        holder.itemBinding.taskItem .setOnClickListener{
            onTaskClickListener.showTaskDescriptionFragment(position)
        }
        holder.itemBinding.deleteTask.setOnClickListener{
            onDeleteClickListener.deleteTask(position)
            holder.itemBinding.swipeLayout.close()
        }

        holder.itemBinding.taskButtonCheck.setOnClickListener()
        {
            onDoneClickListener.taskDone(position)
        }

        if(item?.isDone==true)
        {
            holder.itemBinding.taskTitle.setTextColor(Color.GREEN)
            holder.itemBinding.taskLine.setBackgroundResource(R.drawable.rounded_done_view)
            holder.itemBinding.taskButtonCheck.setBackgroundResource(R.drawable.rounded_done_button)
            holder.itemBinding.taskButtonCheck.isClickable=false
        }
        else
        {

            holder.itemBinding.taskTitle.setTextColor(-10642453)
            holder.itemBinding.taskLine.setBackgroundResource(R.drawable.rounded_view)
            holder.itemBinding.taskButtonCheck.setBackgroundResource(R.drawable.task_button_check_rounded)
            holder.itemBinding.taskButtonCheck.isClickable=true
        }

    }


    lateinit var onDoneClickListener:OnDoneClickListener

    interface OnDoneClickListener
    {
        fun taskDone(position : Int)
    }

    lateinit var onDeleteClickListener:OnDeleteClickListener

    interface OnDeleteClickListener
    {
        fun deleteTask(position : Int)
    }


    lateinit var onTaskClickListener:OnTaskClickListener

    interface OnTaskClickListener
    {
        fun showTaskDescriptionFragment(position : Int)
    }

    fun getTime(date:Long):String
    {
        val dateFormat = SimpleDateFormat("h:mm a")
        return dateFormat.format(Date(date))
    }

    fun changeData(listData:List<Task>?)
    {
        tasksList=listData
        notifyDataSetChanged()
    }


    class ViewHolder(var itemBinding: ItemTaskBinding): RecyclerView.ViewHolder(itemBinding.root)

}