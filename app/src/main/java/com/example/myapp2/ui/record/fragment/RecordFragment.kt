package com.example.myapp2.ui.record.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.myapp2.MainActivity
import com.example.myapp2.camera.CameraActivityViewModel
import com.example.myapp2.databinding.FragmentRecordBinding


class RecordFragment : Fragment() {

    private var _binding: FragmentRecordBinding? = null
    private val binding get() = _binding!!
    private val cameraViewModel: CameraActivityViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val mainActivity = activity as MainActivity
        val adapter : RecordAdapter by lazy {
            RecordAdapter(mainActivity)
        }

        _binding = FragmentRecordBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //绑定adapter
        binding.recordList.adapter = adapter
        binding.recordList.layoutManager = StaggeredGridLayoutManager(1,
            StaggeredGridLayoutManager.VERTICAL)
        cameraViewModel.allItemList.observe(viewLifecycleOwner) {
            adapter.setData(it)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}