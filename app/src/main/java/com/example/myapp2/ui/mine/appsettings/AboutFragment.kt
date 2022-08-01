package com.example.myapp2.ui.mine.appsettings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.example.myapp2.R
import com.example.myapp2.databinding.FragmentAboutBinding
import kotlinx.android.synthetic.main.activity_main.*


class AboutFragment : Fragment() {
    private var _binding: FragmentAboutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAboutBinding.inflate(inflater,container,false)
        //隐藏底部导航
        requireActivity().nav_view.isVisible = false
        //导航
        binding.aboutThank.setOnClickListener {
            findNavController().navigate(R.id.action_aboutFragment_to_navigation_mine)
        }

        return binding.root
    }


}