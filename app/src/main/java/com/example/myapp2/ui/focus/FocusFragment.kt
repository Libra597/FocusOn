package com.example.myapp2.ui.focus

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.myapp2.MainActivity
import com.example.myapp2.camera.CameraActivity
import com.example.myapp2.databinding.FragmentFocusBinding
import java.util.*
import kotlin.concurrent.fixedRateTimer


class FocusFragment : Fragment() {
    //绑定
    private var _binding: FragmentFocusBinding? = null
    private val binding get() = _binding!!
    //轮播所需变量
    private var bannertext:String = ""
    private var  randoms :Int = 0
    lateinit var timer: Timer

    private val handler = object : Handler(Looper.getMainLooper()){
        override fun handleMessage(msg: Message) {
            when(msg.what){
                1 -> {
                    binding.bannerText.setText(bannertext)
                }
            }
        }
    }

    //viewModel
    private val viewModel : FocusViewModel by viewModels()

    companion object{
        //控制banner稳定轮播
        var flag = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //初始化viewModel
        viewModel.insertText()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFocusBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //标语轮播
        if (flag){
            startTimer()
            flag = false
        }else{
            binding.bannerText.text = bannertext
        }
        Log.d("flagHome", flag.toString())

        //开始专注
        binding.homeStart.setOnClickListener {
            binding.homeStart.isVisible = false
            binding.editview.isVisible  = true
        }

        //确认按钮
        var missionInput:String
        val mainActivity = activity as MainActivity
        binding.homeYes.setOnClickListener {
            binding.editview.isVisible = false
            binding.homeStart.isVisible = true
            missionInput = binding.homeEditname.text.toString()
            if (missionInput.isBlank()){
                Toast.makeText(mainActivity,
                    "任务名不能为空哦~",
                    Toast.LENGTH_LONG).show()
            }else{
                val intent = Intent(mainActivity, CameraActivity::class.java)
                intent.putExtra("missionInput",missionInput)
                binding.homeEditname.setText("")
                mainActivity.startActivity(intent)
            }
        }

        //取消按钮
        binding.homeNo.setOnClickListener {
            binding.editview.isVisible = false
            binding.homeStart.isVisible = true
        }
        return root
    }

    //启动计时器轮播
    private fun startTimer() {
        timer = fixedRateTimer("", false, 100, 6000) {
            randoms = (0..5).random()
            val msg = Message()
            msg.what = 1
            bannertext = viewModel.textList[randoms]
            handler.sendMessage(msg)
        }
    }
}