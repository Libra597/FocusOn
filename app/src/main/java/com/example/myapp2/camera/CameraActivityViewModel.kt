package com.example.myapp2.camera

import android.app.Application
import androidx.lifecycle.*
import com.example.myapp2.database.FocusDatabase
import com.example.myapp2.database.Item
import com.example.myapp2.database.ItemRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CameraActivityViewModel(application: Application): AndroidViewModel(application) {
    private val itemDao = FocusDatabase.getDataBase(application).itemDao()
    private val itemRepository:ItemRepository = ItemRepository(itemDao)

    val allItemList: LiveData<List<Item>> = itemRepository.allItemList

    fun insert(item: Item) {
        viewModelScope.launch(Dispatchers.IO) {
            itemRepository.insertItem(item)
        }
    }

    fun updateDes(idNum: Int,Des:String) {
        viewModelScope.launch(Dispatchers.IO) {
            itemRepository.updateDes(idNum,Des)
        }
    }
    fun updateSum(idNum: Int,Sum:String) {
        viewModelScope.launch(Dispatchers.IO) {
            itemRepository.updateSum(idNum,Sum)
        }
    }
    fun updateImage(idNum: Int,Ima:String) {
        viewModelScope.launch(Dispatchers.IO) {
            itemRepository.updateImage(idNum,Ima)
        }
    }

    fun delete(idNum: Int)  {
        viewModelScope.launch(Dispatchers.IO) {
            itemRepository.deleteItem(idNum)
        }
    }

    fun find(idNum:Int) :LiveData<Item>{
        return itemRepository.find(idNum)
    }



}