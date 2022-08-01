package com.example.myapp2.database

import androidx.lifecycle.LiveData

class ItemRepository(val itemDao: ItemDao) {

    val allItemList: LiveData<List<Item>> =itemDao.getAllItem()

    fun insertItem(item: Item){
        itemDao.insertItem(item)
    }

    fun updateDes(idNum: Int,Des:String){
        itemDao.updateDes(idNum,Des)
    }
    fun updateSum(idNum: Int,Sum:String){
        itemDao.updateSum(idNum,Sum)
    }
    fun updateImage(idNum: Int,Ima:String){
        itemDao.updateImage(idNum,Ima)
    }

    fun deleteItem(idNum: Int) {
        itemDao.deleteItem(idNum )
    }
    fun find(idNum:Int) : LiveData<Item>{
        return itemDao.find(idNum)
    }


}