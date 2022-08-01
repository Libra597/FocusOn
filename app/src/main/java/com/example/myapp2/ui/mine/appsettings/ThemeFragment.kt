package com.example.myapp2.ui.mine.appsettings

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.myapp2.MainActivity
import com.example.myapp2.R
import com.example.myapp2.databinding.FragmentThemeBinding
import kotlinx.android.synthetic.main.activity_camera.*
import kotlinx.android.synthetic.main.activity_main.*

class ThemeFragment : Fragment() {

    private var _binding: FragmentThemeBinding? = null
    private val binding: FragmentThemeBinding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentThemeBinding.inflate(inflater, container, false)

        //导入activity和share preference
        val mainActivity = activity as MainActivity
        val share: SharedPreferences = mainActivity.share
        mainActivity.nav_view.isVisible = false

        //设置主题颜色
        binding.thred.setOnClickListener {
            mainActivity.themeNum = 0
            mainActivity.recreate()
            val editer: SharedPreferences.Editor = share.edit()
            editer.putInt("themeNum",mainActivity.themeNum)
            editer.commit()
            findNavController().navigate(R.id.action_themeFragment_to_navigation_mine)
        }

        binding.thyellow.setOnClickListener {
            mainActivity.themeNum = 1
            mainActivity.recreate()
            val editer: SharedPreferences.Editor = share.edit()
            editer.putInt("themeNum",mainActivity.themeNum)
            editer.commit()
            findNavController().navigate(R.id.action_themeFragment_to_navigation_mine)
        }

        binding.thgreen.setOnClickListener {
            mainActivity.themeNum = 2
            mainActivity.recreate()
            val editer: SharedPreferences.Editor = share.edit()
            editer.putInt("themeNum",mainActivity.themeNum)
            editer.commit()
            findNavController().navigate(R.id.action_themeFragment_to_navigation_mine)
        }

        binding.thblue.setOnClickListener {
            mainActivity.themeNum = 3
            mainActivity.recreate()
            val editer: SharedPreferences.Editor = share.edit()
            editer.putInt("themeNum",mainActivity.themeNum)
            editer.apply()
            findNavController().navigate(R.id.action_themeFragment_to_navigation_mine)
        }

        binding.thdark.setOnClickListener {
            mainActivity.themeNum = 4
            mainActivity.recreate()
            val editer: SharedPreferences.Editor = share.edit()
            editer.putInt("themeNum",mainActivity.themeNum)
            editer.commit()
            findNavController().navigate(R.id.action_themeFragment_to_navigation_mine)
        }

        return binding.root
    }


}