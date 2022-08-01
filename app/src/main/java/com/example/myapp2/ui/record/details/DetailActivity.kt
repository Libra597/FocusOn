package com.example.myapp2.ui.record.details


import android.annotation.SuppressLint
import android.app.Activity
import android.content.*
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.myapp2.R
import com.example.myapp2.camera.CameraActivityViewModel
import com.example.myapp2.database.Item
import com.example.myapp2.databinding.ActivityDetailBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*


class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val cameraViewModel: CameraActivityViewModel by viewModels()
    private var mImagePath: String = ""
    private var themeNum: Int = 1
    lateinit var share: SharedPreferences

    companion object{
        var idNum:Int = 0
        val GALLERY = 1
        var IMAGE_DIRECTORY = "FocusImagesFolder"
        private var mImageFileName: String = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //设置主题
        share = getSharedPreferences("appData", MODE_PRIVATE)
        themeNum = share.getInt("themeNum",0)

        when(themeNum){
            0-> setTheme(R.style.Theme_red_NoActionBar)
            1-> setTheme(R.style.Theme_yellow_NoActionBar)
            2-> setTheme(R.style.Theme_green_NoActionBar)
            3-> setTheme(R.style.Theme_blue_NoActionBar)
            else-> setTheme(R.style.Theme_dark_NoActionBar)
        }
        binding = ActivityDetailBinding.inflate(layoutInflater)
        mImageFileName = intent.getStringExtra("mFileName").toString()

        setContentView(binding.root)
        //获取item数据及显示
        val nameobserver :Observer<Item> =
            Observer<Item> { item ->
                binding.toolbarLayout.title = item?.mission
                binding.detailLength.text = "专注时长：" + item?.timelength
                binding.detailStart.text = "开始时间：" + item?.timeStart
                binding.detailDescribe.text = item?.description
                binding.detailSummary.text  =  item?.summary
                binding.editDescribe.setText(item?.description)
                binding.editSummary.setText(item?.summary)
                if (item?.imageSrc != ""){
                    mImagePath = item?.imageSrc.toString()
                    Glide.with(this@DetailActivity)
                        .load(mImagePath)
                        .centerCrop()
                        .into(binding.detailImage)
                }
                mImageFileName = item?.mission.toString()
            }
        idNum = intent.getIntExtra("Id",0)
        val itemNow: LiveData<Item> = cameraViewModel.find(idNum)
        itemNow.observe(this,nameobserver)
        setSupportActionBar(binding.toolbar)

        //删除按钮
        binding.detailDelete.setOnClickListener {
            //弹出对话框
            val dialog: android.app.AlertDialog? = android.app.AlertDialog.Builder(this)
                .setTitle("确定删除吗？") //设置对话框的标题
                //设置对话框的按钮
                .setNegativeButton("取消", DialogInterface.OnClickListener { dialog, which ->
                    Toast.makeText(this@DetailActivity, "已取消~", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                })
                .setPositiveButton("确定", DialogInterface.OnClickListener { dialog, which ->
                    cameraViewModel.delete(idNum)
                    Toast.makeText(this@DetailActivity, "已删除~", Toast.LENGTH_SHORT).show()
                    finish()
                    dialog.dismiss()
                }).create()
            dialog?.show()
        }
        //修改按钮
        binding.detailChange.setOnClickListener {
            binding.buttonList1.isVisible = false
            binding.buttonList2.isVisible = true
            binding.editDescribe.isVisible= true
            binding.editSummary.isVisible = true
            binding.detailDescribe.isVisible = false
            binding.detailSummary.isVisible  = false
        }

        //取消按钮
        binding.detailCancel.setOnClickListener {
            binding.buttonList1.isVisible = true
            binding.buttonList2.isVisible = false
            binding.editDescribe.isVisible= false
            binding.editSummary.isVisible = false
            binding.detailDescribe.isVisible = true
            binding.detailSummary.isVisible  = true
        }
        //确定按钮
        binding.detailEnter.setOnClickListener {
            binding.buttonList1.isVisible = true
            binding.buttonList2.isVisible = false
            binding.editDescribe.isVisible= false
            binding.editSummary.isVisible = false
            binding.detailDescribe.isVisible = true
            binding.detailSummary.isVisible  = true
            //修改数据
            var desStr: String = binding.editDescribe.text.toString()
            var sumStr: String = binding.editSummary.text.toString()
            cameraViewModel.updateDes(idNum,desStr)
            cameraViewModel.updateSum(idNum,sumStr)

            Toast.makeText(this@DetailActivity, "已修改~", Toast.LENGTH_SHORT).show()
        }

        //浮动按钮
        binding.fab.setOnClickListener {
                    val galleryIntent = Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(galleryIntent, GALLERY)
        }

        //Photo / Vlog ，RecyclerView展示相册
        var ImageList = ArrayList<String>()
        ImageList = getImagePathFromIS() as ArrayList<String>
        val photoAdapter:PhotoAdapter by lazy {
            PhotoAdapter(this@DetailActivity,ImageList)
        }
        binding.photoList.adapter = photoAdapter
        binding.photoList.layoutManager = StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)

    }

    //获取图片
    private fun getImagePathFromIS(): List<String>? {
        // 图片列表
        val imagePathList: MutableList<String> = ArrayList()
        // 得到image文件夹的路径
        mImageFileName = getExternalFilesDir(mImageFileName).toString()
        val filePath = mImageFileName
        Log.d("mImageFileName",mImageFileName)

        // 得到该路径文件夹下所有的文件
        val fileAll = File(filePath)
        val files = fileAll.listFiles()
        // 将所有的文件存入ArrayList中,并过滤所有图片格式的文件
        for (i in files.indices) {
            val file = files[i]
            if (checkIsImageFile(file.path)) {
                imagePathList.add(file.path)
            }
        }
        // 返回得到的图片列表
        return imagePathList
    }

    //判定类型
    @SuppressLint("DefaultLocale")
    private fun checkIsImageFile(fName: String): Boolean {
        var isImageFile = false
        // 获取扩展名
        val FileEnd = fName.substring(
            fName.lastIndexOf(".") + 1,
            fName.length
        ).lowercase(Locale.getDefault())
        isImageFile =
            if (FileEnd == "jpg" || FileEnd == "png" || FileEnd == "bmp" || FileEnd == "jpeg" || FileEnd == "mp4") {
                true
            } else {
                false
            }
        return isImageFile
    }

    //加载并存储任务顶部icon
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == GALLERY){
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
                                    mImagePath = saveImageToInternalStorage(bitmap).toString()
                                    cameraViewModel.updateImage(idNum,mImagePath)

                                    Log.d("ImagePath", mImagePath)
                                }
                                return false
                            }
                        }
                        )
                        .into(binding.detailImage)
                }
            }
        }else if(resultCode == Activity.RESULT_CANCELED){
            Log.d("Cancelled", "User have cancelled image selection")
        }
    }

    //存储任务顶部icon
    private fun saveImageToInternalStorage(bitmap: Bitmap):String{
        val wrapper = ContextWrapper(applicationContext)
        var file = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_ENABLE_WRITE_AHEAD_LOGGING)
        var filename = file.toString()
        Log.d("filenameee:",filename)
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