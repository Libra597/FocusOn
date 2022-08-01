package com.example.myapp2.ui.mine.appsettings

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.myapp2.MainActivity
import com.example.myapp2.R
import com.example.myapp2.camera.CameraActivity
import com.example.myapp2.databinding.FragmentFocusBinding
import com.example.myapp2.databinding.FragmentTargetBinding
import kotlinx.android.synthetic.main.activity_main.*

class TargetFragment : Fragment() {
    private var _binding: FragmentTargetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentTargetBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val mainActivity = activity as MainActivity
        val share = mainActivity.share

        //隐藏底部导航
        requireActivity().nav_view.isVisible = false

        //取消
        binding.targetCancel.setOnClickListener {
            findNavController().navigate(R.id.action_targetFragment_to_navigation_mine)
        }

        //确认更新
        binding.targetEnter.setOnClickListener {
            val editor = share.edit()
            editor.putInt("target",binding.editTextTextPersonName2.text.toString().toInt())
            editor.putInt("finish",binding.editTextTextPersonName3.text.toString().toInt())
            Log.d("input:","target:${binding.editTextTextPersonName2.text.toString().toInt()}")
            editor.apply()
            findNavController().navigate(R.id.action_targetFragment_to_navigation_mine)
        }

        return root
    }


}