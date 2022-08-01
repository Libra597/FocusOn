package com.example.myapp2

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.room.Room
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.myapp2.database.FocusDatabase
import com.example.myapp2.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    //数据库
    val focusDatabase:FocusDatabase by lazy {
        Room.databaseBuilder(
            this,FocusDatabase::class.java,
            "focusDatabase.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
    var themeNum:Int = 1        //获取主题
    lateinit var share: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //获取sharePreference
        share = getSharedPreferences("appData", MODE_PRIVATE)
        themeNum = share.getInt("themeNum",0)

        //切换主题
        when(themeNum){
            0-> setTheme(R.style.Theme_red)
            1-> setTheme(R.style.Theme_yellow)
            2-> setTheme(R.style.Theme_green)
            3-> setTheme(R.style.Theme_blue)
            else-> setTheme(R.style.Theme_dark)
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //绑定导航
        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        //委托back健
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_focus, R.id.navigation_record, R.id.navigation_mine
                ,R.id.themeFragment,R.id.aboutFragment,R.id.targetFragment
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    //设置个人头像
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            //请求码为1
            if(requestCode == 1){
                data?.let {
                    val selectedImageUri = data.data
                    Glide.with(this)
                        .load(selectedImageUri)
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .listener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                                Log.e("FAILED", "Error in loading image")
                                return false
                            }

                            override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                                resource?.let {
                                    val bitmap: Bitmap = resource.toBitmap()
                                    val mImagePath:String = saveImageToInternalStorage(bitmap).toString()
                                    val editor = share.edit()
                                    Log.d("putImage",mImagePath)
                                    editor.putString("mImagePath",mImagePath)
                                    editor.apply()
                                    Log.d("ImagePath", mImagePath)
                                }
                                return false
                            }
                        }
                        )
                        .into(findViewById(R.id.self_icon))
                }
            }
        }else if(resultCode == Activity.RESULT_CANCELED){
            Log.d("Cancelled", "User have cancelled image selection")
        }
    }

    private fun saveImageToInternalStorage(bitmap: Bitmap):String{
        val wrapper = ContextWrapper(applicationContext)
        var file = wrapper.getDir("FocusImagesFolder", Context.MODE_ENABLE_WRITE_AHEAD_LOGGING)
        var filename = file.toString()
        Log.d("filenamee:",filename)
        file = File(file, "${UUID.randomUUID()}.jpg")

        try {
            val steam : OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, steam)
            steam.flush()
            steam.close()
        }catch (e: IOException){
            e.printStackTrace()
        }
        return file.absolutePath
    }

}