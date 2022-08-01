package com.example.myapp2.ui.record.details

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapp2.R
import java.io.File


class PhotoAdapter(private val _activity: Activity, var dataList: ArrayList<String>):
    RecyclerView.Adapter<PhotoAdapter.myViewHolder>() {

        inner class myViewHolder(view: View):RecyclerView.ViewHolder(view){
            val photo: ImageView =view.findViewById(R.id.photo_image)

        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        val viewHolder = myViewHolder(view)

        return viewHolder
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        //加载图片
        val imageSrc = dataList[position].toString()
        Glide.with(_activity)
            .load(imageSrc)
            .centerCrop()
            .into(holder.photo)
        //点击利用系统相册打开/编辑图片
        holder.photo.setOnClickListener {
            openPhoto(imageSrc)
        }

    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun openPhoto(filePath: String?) {
        val file = File(filePath)
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        intent.setDataAndType(getImageContentUri(file), "image/*")
        _activity.startActivity(intent)
    }

    fun getImageContentUri(imageFile: File): Uri? {
        val filePath: String = imageFile.getAbsolutePath()
        val cursor: Cursor? = _activity.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, arrayOf(MediaStore.Images.Media._ID),
            MediaStore.Images.Media.DATA + "=? ", arrayOf(filePath), null
        )
        return if (cursor != null && cursor.moveToFirst()) {
            val id: Int = cursor.getInt(
                cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID)
            )
            val baseUri: Uri = Uri.parse("content://media/external/images/media")
            Uri.withAppendedPath(baseUri, "" + id)
        } else {
            if (imageFile.exists()) {
                val values = ContentValues()
                values.put(MediaStore.Images.Media.DATA, filePath)
                _activity.contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values
                )
            } else {
                null
            }
        }
    }


}