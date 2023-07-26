package com.projects.todoapp.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.projects.todoapp.R
import com.projects.todoapp.ui.addTask.AddTaskFragment
import com.projects.todoapp.ui.addTask.OnDismissListener
import com.projects.todoapp.databinding.ActivityMainBinding
import com.projects.todoapp.ui.settings.SettingsFragment
import com.projects.todoapp.ui.tasksList.ListFragment
import kotlinx.coroutines.launch
import java.util.*


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private var tasksListFragment: ListFragment?=null
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
            if(supportFragmentManager.findFragmentByTag("settingsFragment")!=null)
            {
                tasksSettingsFragment=supportFragmentManager.findFragmentByTag("settingsFragment") as SettingsFragment
                registerChangeActivityTitleCallBacks(setListTitle = true,setSettingsTitle = true)
                registerDestroyedFragmentsCallBacks(setListCallbacks = true,setSettingsCallbacks = true)
            }
            else
            {
                registerChangeActivityTitleCallBacks(setListTitle = true,setSettingsTitle = false)
                registerDestroyedFragmentsCallBacks(setListCallbacks = true,setSettingsCallbacks = false)
            }
            if(ListFragment.currentDate==null)
            {
                ListFragment.currentDate=Calendar.getInstance()
            }
            ListFragment.currentDate?.timeInMillis=savedInstanceState.getLong("CurrentDate")
            if (savedInstanceState.getLong("timeOfNewInsertedTask")!=0L)
            {
                tasksListFragment!!.timeOfNewInsertedTask=savedInstanceState.getLong("timeOfNewInsertedTask")
            }
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
        val addTaskFragment= AddTaskFragment()

        addTaskFragment.onDismissListener=object : OnDismissListener
        {
            override fun bottomSheetFragmentDismissed() {
                lifecycleScope.launch {
                    if (tasksListFragment?.isResumed == true)
                    {
                        ListFragment?.tasksList=null
                        tasksListFragment?.loadData()
                        tasksListFragment?.scrollToAddedTask(addTaskFragment.timeOfNewInsertedTask)
                    }
                    if(tasksSettingsFragment?.isResumed == true)
                    {
                        ListFragment?.tasksList=null
                        tasksListFragment?.timeOfNewInsertedTask=addTaskFragment.timeOfNewInsertedTask
                    }
                }
            }
        }
        addTaskFragment.show(supportFragmentManager,"")
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
            tasksListFragment?.onResumedListener=object : ListFragment.OnFragmentStarted
            {
                override fun bindActivityTitle() {
                    binding.title.text="To Do List"
                }
            }
        }
        if (setSettingsTitle)
        {
            tasksSettingsFragment?.onResumedListener=object : SettingsFragment.OnFragmentStarted
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
            ListFragment.tasksList=null
            ListFragment.currentDate= Calendar.getInstance()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val NightMode = AppCompatDelegate.getDefaultNightMode()
        val sharedPreferences = getSharedPreferences("SharedPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("NightModeInt", NightMode)
        editor.apply()
        outState.putLong("CurrentDate", ListFragment.currentDate?.timeInMillis!!)
        if (tasksListFragment?.timeOfNewInsertedTask!=null)
        {
            outState.putLong("timeOfNewInsertedTask", tasksListFragment?.timeOfNewInsertedTask!!)
        }
    }
}