package com.projects.todoapp.settings

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.projects.todoapp.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {
    lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentSettingsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerChangeModeCallBacks(requireActivity().resources.configuration)
    }

    private fun registerChangeModeCallBacks(configuration: Configuration)
    {
        val currentNightMode = configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)

        if (currentNightMode == Configuration.UI_MODE_NIGHT_NO) {
            binding.changeMode.text="Light"
        } else {
            binding.changeMode.text = "Dark"
        }

        binding.changeMode.setOnClickListener{
            when (currentNightMode) {
                Configuration.UI_MODE_NIGHT_NO -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }
                Configuration.UI_MODE_NIGHT_YES -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            }
        }
    }

    var onResumedListener:OnFragmentStarted?=null

    interface OnFragmentStarted
    {
        fun bindActivityTitle()
    }

    override fun onStart() {
        super.onStart()
        onResumedListener?.bindActivityTitle()
    }

    var onFragmentDestroyedListener:OnFragmentDestroyed?=null

    interface OnFragmentDestroyed
    {
        fun onDestroyed()
    }

    override fun onDestroy() {
        super.onDestroy()
        onFragmentDestroyedListener?.onDestroyed()
    }
}