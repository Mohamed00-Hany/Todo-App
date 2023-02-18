package com.projects.todoapp.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Task (
    @PrimaryKey(autoGenerate = true) var id:Int=0,
    @ColumnInfo var title:String?=null,
    @ColumnInfo var description:String?=null,
    @ColumnInfo var date:Long?=null,
    @ColumnInfo var time:Long?=null,
    @ColumnInfo var isDone:Boolean?=false
)