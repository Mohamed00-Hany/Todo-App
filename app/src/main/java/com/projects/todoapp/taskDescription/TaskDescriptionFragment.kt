package com.projects.todoapp.taskDescription

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.projects.todoapp.databinding.FragmentTaskDescriptionBinding

class TaskDescriptionFragment : BottomSheetDialogFragment() {
    lateinit var binding: FragmentTaskDescriptionBinding
    var description:String?=null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentTaskDescriptionBinding.inflate(inflater,container,false)
        binding.taskDescriptionText.text=description
        return binding.root
    }

    fun setTaskDescription(description: String?)
    {
        this.description=description
    }

}