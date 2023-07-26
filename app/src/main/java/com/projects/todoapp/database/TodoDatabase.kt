package com.projects.todoapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.projects.todoapp.database.dao.TaskDao
import com.projects.todoapp.database.model.Task

@Database(entities = [Task::class], version = 1, exportSchema = false)
abstract class TodoDatabase :RoomDatabase() {

    abstract fun getTaskDao():TaskDao

    companion object
    {
        private const val databaseName="TodoDatabase"
        private var myDatabase:TodoDatabase?=null

        @Synchronized
        fun getDatabase(context:Context):TodoDatabase
        {
            if(myDatabase==null)
            {
                myDatabase= Room
                    .databaseBuilder(context,TodoDatabase::class.java,databaseName)
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return myDatabase!!
        }
    }

}