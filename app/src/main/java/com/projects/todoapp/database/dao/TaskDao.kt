package com.projects.todoapp.database.dao

import androidx.room.*
import com.projects.todoapp.database.model.Task

@Dao
interface TaskDao {
    @Insert
    suspend fun insertTask(task:Task)
    @Delete
    suspend fun deleteTask(task:Task)
    @Update
    suspend fun updateTask(task:Task)
    @Query("select * from Task")
    suspend fun getAllTasks():List<Task>
    @Query("delete from Task")
    suspend fun deleteAllTasks()
    @Query("select * from Task where date= :date order by time")
    suspend fun getTasksByDate(date:Long):List<Task>
}