package com.example.myapp2.ui.record.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapp2.MainActivity
import com.example.myapp2.R
import com.example.myapp2.database.Item
import com.example.myapp2.databinding.ItemRecordBinding
import com.example.myapp2.ui.record.details.DetailActivity
import kotlin.coroutines.coroutineContext


    //Record列表的Adapter
class RecordAdapter(private val _activity:Activity) : RecyclerView.Adapter<RecordAdapter.MyViewHolder>() {
    var dataList = emptyList<Item>()

    class MyViewHolder(private val binding: ItemRecordBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("ResourceAsColor")
        fun bind(item: Item) {
            binding.missionName.text = item.mission
            binding.missionLength.text = "专注时长：" + item.timelength
            binding.missionStart.text = "开始时间：" + item.timeStart
        }

        companion object {
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemRecordBinding.inflate(layoutInflater, parent, false)
                return MyViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = dataList[position]
        //加载图标
        Glide.with(_activity)
            .load(item.imageSrc)
            .centerCrop()
            .into(holder.itemView.findViewById(R.id.mission_icon))
        holder.itemView.findViewById<LinearLayout>(R.id.missionItem).setOnClickListener {
            //导航或传送
            val intent = Intent(_activity,DetailActivity::class.java)
            intent.putExtra("Id",item.id)
            val mFileName :String  = item.mission.toString() + "-" + item.timeStart.toString()
            intent.putExtra("mFileName", mFileName )
            _activity.startActivity(intent)
        }
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun setData(itemData: List<Item>) {
        val itemDiffUtil= RecordDiffUtil(dataList,itemData)
        val itemDiffResult= DiffUtil.calculateDiff(itemDiffUtil)
        this.dataList=itemData
        itemDiffResult.dispatchUpdatesTo(this)
    }

}