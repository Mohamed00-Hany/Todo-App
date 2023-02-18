package com.projects.todoapp.activities

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.NightMode
import androidx.fragment.app.Fragment
import androidx.fragment.app.ListFragment
import com.projects.todoapp.R
import com.projects.todoapp.databinding.ActivityMainBinding
import com.projects.todoapp.fragments.AddTaskFragment
import com.projects.todoapp.fragments.SettingsFragment
import com.projects.todoapp.OnDismissListener
import kotlin.math.log

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    var tasksListFragment = com.projects.todoapp.fragments.ListFragment()
    val tasksSettingsFragment=SettingsFragment()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.bottomNavigationView.setOnItemSelectedListener{
            when(it.itemId)
            {
                R.id.item_list->
                {
                    binding.title.text="To Do List"
                    showFragment(tasksListFragment)
                }
                R.id.item_settings->
                {
                    binding.title.text="Settings"
                    showFragment(tasksSettingsFragment)
                }
            }
            return@setOnItemSelectedListener true
        }

        binding.bottomNavigationView.selectedItemId=R.id.item_list

        binding.addTaskButton.setOnClickListener{
            showBottomSheetFragment()
        }

    }

    override fun onNightModeChanged(mode: Int) {
        super.onNightModeChanged(mode)
        binding.bottomNavigationView.selectedItemId=R.id.item_list
    }


    private fun showFragment(fragment: Fragment)
    {
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container,fragment).commit()
    }
    private fun showBottomSheetFragment()
    {
        val fragment=AddTaskFragment()

        fragment.onDismissListener=object : OnDismissListener
        {
            override fun BottomSheetFragmentDismissed() {
                tasksListFragment.loadData()
            }
        }

        fragment.show(supportFragmentManager,"")
    }

}