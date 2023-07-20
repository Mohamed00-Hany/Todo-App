package com.projects.todoapp.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.projects.todoapp.R
import com.projects.todoapp.databinding.ActivityMainBinding
import com.projects.todoapp.addTask.AddTaskFragment
import com.projects.todoapp.settings.SettingsFragment
import com.projects.todoapp.addTask.OnDismissListener
import com.projects.todoapp.tasksList.CurrentDate
import com.projects.todoapp.tasksList.ListFragment
import java.util.*

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private var tasksListFragment:ListFragment?=null
    private var tasksSettingsFragment: SettingsFragment?=null
    var modeChanged=false
    var backFromSettingsFragment=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState!=null)
        {
            tasksListFragment=supportFragmentManager.findFragmentByTag("listFragment") as ListFragment
            tasksSettingsFragment=supportFragmentManager.findFragmentByTag("settingsFragment") as SettingsFragment
            registerChangeActivityTitleCallBacks(setListTitle = true,setSettingsTitle = true)
            registerDestroyedFragmentsCallBacks(setListCallbacks = true,setSettingsCallbacks = true)
        }

        registerBottomNavigationViewClicks()

        if(savedInstanceState==null)
        {
            binding.bottomNavigationView.selectedItemId=R.id.item_list
        }



    }

    override fun onResume() {
        super.onResume()
        binding.addTaskButton.setOnClickListener{
            showBottomSheetFragment()
        }
    }

    private fun registerBottomNavigationViewClicks()
    {
        binding.bottomNavigationView.setOnItemSelectedListener{
            when(it.itemId)
            {
                R.id.item_list->
                {
                    if (tasksListFragment==null)
                    {
                        tasksListFragment= ListFragment()
                        registerChangeActivityTitleCallBacks(setListTitle = true)
                        registerDestroyedFragmentsCallBacks(setListCallbacks = true)
                        showFragment(tasksListFragment!!)
                    }
                    else if (!tasksListFragment!!.isResumed && !backFromSettingsFragment)
                    {
                        supportFragmentManager.popBackStack()
                    }
                }
                R.id.item_settings->
                {
                    if (tasksSettingsFragment==null)
                    {
                        tasksSettingsFragment= SettingsFragment()
                        registerChangeActivityTitleCallBacks(setSettingsTitle = true)
                        registerDestroyedFragmentsCallBacks(setSettingsCallbacks = true)
                        showFragment(tasksSettingsFragment!!)
                    }
                    else if (!tasksSettingsFragment!!.isResumed)
                    {
                        showFragment(tasksSettingsFragment!!)
                    }
                }
            }
            return@setOnItemSelectedListener true
        }
    }


    override fun onNightModeChanged(mode: Int) {
        super.onNightModeChanged(mode)
        modeChanged=true
    }

    private fun showFragment(fragment: Fragment)
    {
        if (fragment is ListFragment)
        {
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container,fragment,"listFragment").addToBackStack(null).commit()
        }
        else if(fragment is SettingsFragment)
        {
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container,fragment,"settingsFragment").addToBackStack(null).commit()
        }
    }

    private fun showBottomSheetFragment()
    {
        val fragment= AddTaskFragment()

        fragment.onDismissListener=object : OnDismissListener
        {
            override fun BottomSheetFragmentDismissed() {
                tasksListFragment?.loadData()
                tasksListFragment?.scrollToAddedTask()
            }
        }
        fragment.show(supportFragmentManager,"")
    }

    private fun registerDestroyedFragmentsCallBacks(setListCallbacks:Boolean=false, setSettingsCallbacks:Boolean=false)
    {
        if (setListCallbacks)
        {
            tasksListFragment?.onFragmentDestroyedListener=object : ListFragment.OnFragmentDestroyed
            {
                override fun onDestroyed() {
                    if (!modeChanged)
                    {
                        finish()
                    }
                }
            }
        }
        if (setSettingsCallbacks)
        {
            tasksSettingsFragment?.onFragmentDestroyedListener=object : SettingsFragment.OnFragmentDestroyed
            {
                override fun onDestroyed() {
                    if (!modeChanged)
                    {
                        backFromSettingsFragment=true
                        binding.bottomNavigationView.selectedItemId=R.id.item_list
                        backFromSettingsFragment=false
                    }
                }
            }
        }
    }

    private fun registerChangeActivityTitleCallBacks(setListTitle:Boolean=false,setSettingsTitle:Boolean=false)
    {
        if (setListTitle)
        {
            tasksListFragment?.onResumedListener=object :ListFragment.OnFragmentStarted
            {
                override fun bindActivityTitle() {
                    binding.title.text="To Do List"
                }
            }
        }
        if (setSettingsTitle)
        {
            tasksSettingsFragment?.onResumedListener=object :SettingsFragment.OnFragmentStarted
            {
                override fun bindActivityTitle() {
                    binding.title.text="Settings"
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!modeChanged)
        {
            CurrentDate.currentDate= Calendar.getInstance()
        }
    }

}