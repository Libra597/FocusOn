package com.example.myapp2.ui.mine

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.myapp2.MainActivity
import com.example.myapp2.R
import com.example.myapp2.databinding.FragmentMineBinding
import com.example.myapp2.ui.record.details.DetailActivity.Companion.GALLERY
import kotlinx.android.synthetic.main.activity_main.*


class MineFragment : Fragment() {

    private var _binding: FragmentMineBinding? = null
    private val binding get() = _binding!!

    //昵称和签名
    private lateinit var selfName:String
    private lateinit var selfSign:String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMineBinding.inflate(inflater, container, false)

        //读取数据
        val mainActivity = activity as MainActivity
        val share = mainActivity.share
        selfName = share.getString("selfName","J.y.q").toString()
        selfSign = share.getString("selfSign","去翻山外的山。").toString()

        //导航
        binding.setTheme.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_mine_to_themeFragment)
        }
        binding.selfTarget.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_mine_to_targetFragment)
        }
        binding.setInformation.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_mine_to_aboutFragment)
        }

        //修改头像
        binding.selfIcon.setOnClickListener {
            val galleryIntent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                mainActivity.startActivityForResult(galleryIntent, 1)
        }

        //修改昵称
        binding.selfName.setOnClickListener {
            val view: View = layoutInflater.inflate(R.layout.half_dialog_view, null)
            val editText = view.findViewById<View>(R.id.dialog_edit) as EditText
            editText.setText(selfName)
            val dialog: android.app.AlertDialog? = android.app.AlertDialog.Builder(mainActivity)
                .setTitle("输入昵称") //设置对话框的标题
                .setView(view)
                .setNegativeButton("取消",
                    DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
                .setPositiveButton("确定", DialogInterface.OnClickListener { dialog, which ->
                    selfName = editText.text.toString()
                    val editor = share.edit()
                    editor.putString("selfName",selfName)
                    editor.apply()
                    binding.selfName.text = selfName
//                    Toast.makeText(requireContext(), sefName, Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }).create()
            dialog?.show()
        }

        //修改个性签名
        binding.selfSign.setOnClickListener {
            val view: View = layoutInflater.inflate(R.layout.half_dialog_view, null)
            val editText = view.findViewById<View>(R.id.dialog_edit) as EditText
            editText.setText(selfSign)
            val dialog: android.app.AlertDialog? = android.app.AlertDialog.Builder(mainActivity)
                .setTitle("输入个性签名") //设置对话框的标题
                .setView(view)
                .setNegativeButton("取消",
                    DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
                .setPositiveButton("确定", DialogInterface.OnClickListener { dialog, which ->
                    selfName = editText.text.toString()
                    val editor = share.edit()
                    editor.putString("selfSign",selfSign)
                    editor.apply()
                    binding.selfSign.text = selfSign
//                    Toast.makeText(requireContext(), sefName, Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }).create()
            dialog?.show()
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val mainActivity = activity as MainActivity
        mainActivity.nav_view.isVisible = true

        //设置progress，显示完成进度百分比
        val share = mainActivity.share
        val target = share.getInt("target",3)
        val finish = share.getInt("finish",1)
        binding.progressBar.progress = finish * 100 / target

        //加载头像
        val mImagePath:String = share.getString("mImagePath","error").toString()
        Log.d("mImagePath",mImagePath)
        if (mImagePath != "error"){
            Glide.with(mainActivity)
                .load(mImagePath)
                .centerCrop()
                .into(binding.selfIcon)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}