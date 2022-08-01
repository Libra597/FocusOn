package com.example.myapp2.ui.focus

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FocusViewModel(application: Application) : AndroidViewModel(application) {
    var textList = ArrayList<String>()

    fun insertText() {
        textList.add("只为成功找方法，\n不为失败找理由。")
        textList.add("有志者自有千计万计，\n无志者只感千难万难。")
        textList.add("宝剑锋从磨砺出。")
        textList.add("所谓光辉岁月，\n并不是以后闪耀的日子，\n而是无人问津时，对梦想的偏执。")
        textList.add("种一棵树最好的时间是十年前，\n\t其次是现在。")
        textList.add("不积跬步无以至千里，\n不积小流无以成江海。")
    }


}