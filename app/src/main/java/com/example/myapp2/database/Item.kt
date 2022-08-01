package com.example.myapp2.database

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity( tableName = "item_table")
data class Item(
    @PrimaryKey(autoGenerate = true)
    var id: Int ,
    var mission:String ,
    var timeStart:String,
    var timelength:String,

    var description:String,
    var summary:String,
    var imageSrc:String,

): Parcelable


