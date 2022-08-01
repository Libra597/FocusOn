package com.example.myapp2.database

import android.os.FileObserver.DELETE
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ItemDao {
    //插入数据
    @Insert
    fun insertItem(item: Item)

    //读取单条item
    @Query("SELECT * FROM ITEM_TABLE WHERE  id = :idNum ")
    fun find(idNum: Int): LiveData<Item>

    //读取列表
    @Query("SELECT * FROM ITEM_TABLE ORDER BY ID DESC")
    fun getAllItem(): LiveData<List<Item>>

    //更新任务描述
    @Query("UPDATE ITEM_TABLE SET description = :DES WHERE id = :idNum ")
    fun updateDes(idNum: Int,DES:String)

    //更新任务总结
    @Query("UPDATE ITEM_TABLE SET summary = :SUM WHERE id = :idNum ")
    fun updateSum(idNum: Int,SUM:String)

    //更新任务图标
    @Query("UPDATE ITEM_TABLE SET imageSrc = :Ima WHERE id = :idNum ")
    fun updateImage(idNum: Int,Ima:String)

    //删除
    @Query("DELETE FROM ITEM_TABLE WHERE id = :idNum")
    fun deleteItem(idNum: Int)

}