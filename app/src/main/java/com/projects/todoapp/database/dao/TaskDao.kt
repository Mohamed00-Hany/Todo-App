package com.projects.todoapp.database.dao

import androidx.room.*
import com.projects.todoapp.database.model.Task

@Dao
interface TaskDao {
    @Insert
    fun insertTask(task:Task)
    @Delete
    fun deleteTask(task:Task)
    @Update
    fun updateTask(task:Task)
    @Query("select * from Task")
    fun getAllTasks():List<Task>
    @Query("delete from Task")
    fun deleteAllTasks()
    @Query("select * from Task where date= :date")
    fun getTasksByDate(date:Long):List<Task>
}