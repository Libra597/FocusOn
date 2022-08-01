package com.example.myapp2.camera

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.*
import android.provider.MediaStore
import android.text.format.DateFormat
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.*
import androidx.camera.video.VideoCapture
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.core.view.isVisible
import com.example.myapp2.R
import com.example.myapp2.database.Item
import com.example.myapp2.databinding.ActivityCameraBinding
import java.io.File
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors



class CameraActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCameraBinding


    private var imageCapture: ImageCapture? = null

    //Camera组件
    private var cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null
    private var preview: Preview? = null
    private lateinit var cameraExecutor: ExecutorService

    //当前系统时间
    private val sysTimeStart = System.currentTimeMillis()
    private val sysTimeStartStr :CharSequence = DateFormat.format("yyyy-MM-dd HH:mm:ss", sysTimeStart)//24h显示

    //所需用于命名的变量
    lateinit var timeLength : String
    private var missionName:String = ""

    val cameraViewModel: CameraActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)

        //全屏沉浸式设置
        supportActionBar?.hide()
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(binding.root)
//        title = "专注中"

        //获取missionName
        missionName = intent.getStringExtra("missionInput").toString()

        //权限请求
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        //屏幕旋转
        imageCapture = ImageCapture.Builder().build()
        val orientationEventListener = object : OrientationEventListener(this as Context) {
            override fun onOrientationChanged(orientation : Int) {
                // Monitors orientation values to determine the target rotation value
                val rotation : Int = when (orientation) {
                    in 45..134 -> Surface.ROTATION_270
                    in 135..224 -> Surface.ROTATION_180
                    in 225..314 -> Surface.ROTATION_90
                    else -> Surface.ROTATION_0
                }
                imageCapture!!.targetRotation = rotation
                Log.d(TAG,"角度：$rotation")
            }
        }
        orientationEventListener.enable()


        //单击事件
        var visible: Boolean = true
        binding.viewFinder.setOnTouchListener { view, event ->
            //隐藏状态按钮
            if (visible){
                binding.topview.isVisible = false
                binding.toptext.isVisible = false
                binding.bottomview.isVisible = false

                visible = false
            }
            else{
                binding.topview.isVisible = true
                binding.toptext.isVisible = true
                binding.bottomview.isVisible = true

                visible = true
            }
            //自动对焦
            val action = FocusMeteringAction.Builder(
                binding.viewFinder.getMeteringPointFactory()
                    .createPoint(event.getX(), event.getY())
            ).build()
            showTapView(event.x.toInt(), event.y.toInt())
            camera?.getCameraControl()?.startFocusAndMetering(action)
            true
        }


        //打开闪光灯
        binding.light.setOnClickListener{
            val mCameraInfo = camera?.cameraInfo
            val mCameraControl = camera?.cameraControl

            if (lightflag ){
                if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA){
                    //后置闪光
                    mCameraControl?.enableTorch(lightflag)
                }else{
                    //前置闪光
                    binding.light1.setBackgroundColor(Color.WHITE)
                    binding.light2.setBackgroundColor(Color.WHITE)
                    binding.light3.setBackgroundColor(Color.WHITE)
                    binding.light4.setBackgroundColor(Color.WHITE)
                }
                binding.light.setImageResource(R.drawable.ic_flash_off_vector)
                lightflag = false
            }else{  //关闭闪光灯
                if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA){
                    mCameraControl?.enableTorch(lightflag)
                }else{
                    binding.light1.setBackgroundColor(Color.TRANSPARENT)
                    binding.light2.setBackgroundColor(Color.TRANSPARENT)
                    binding.light3.setBackgroundColor(Color.TRANSPARENT)
                    binding.light4.setBackgroundColor(Color.TRANSPARENT)
                }
                lightflag = true
                binding.light.setImageResource(R.drawable.ic_flash_on_vector)
            }
        }

        //切换摄像头
        binding.turn.setOnClickListener {
            if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
                binding.turn.setImageResource(R.drawable.ic_camera_rear_vector)
                startCamera()
            }
            else{
                cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                binding.turn.setImageResource(R.drawable.ic_camera_front_vector)
                startCamera()
            }
        }


        //拍照、摄像
        binding.imageCaptureButton.setOnClickListener { takePhoto() }
        binding.videoCaptureButton.setOnClickListener { captureVideo() }

        cameraExecutor = Executors.newSingleThreadExecutor()



        //在主线程里面处理消息并更新Timer
        val mHandler: Handler = object : Handler() {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                when (msg.what) {
                    1 -> {
                        val sysTime = System.currentTimeMillis() //获取系统时间
                        val sysTimeStr: CharSequence = DateFormat.format("yyyy-MM-dd HH:mm:ss", sysTime) //时间显示格式
                        binding.timeNow.setText(sysTimeStr) //更新时间
                        //计时器
                        var timer = sysTime - sysTimeStart
                        var sec = timer / 1000 % 60
                        var min = timer / 1000 % 3600 /60
                        var hour = timer/ 1000 / 3600
                        timeLength = hour.toString()+":" +min.toString() +':'+ sec.toString()
                        binding.textTimer.setText("已专注：$hour:$min:$sec")
                    }
                    else -> {}
                }
            }
        }

        class TimeThread : Thread() {
            override fun run() {
                do {
                    try {
                        sleep(990)
                        val msg = Message()
                        msg.what = 1 //消息码
                        mHandler.sendMessage(msg) // 每隔不到1秒发送一个msg给mHandler
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                } while (true)
            }
        }
        TimeThread().start() //启动新的线程


        //完成按钮
        binding.save.setOnClickListener {
            val timeStart:String = sysTimeStartStr.toString()
            val newData = Item(
                0,
                missionName,
                timeStart,
                timeLength,
                "","",""
            )                                           //更新数据库
            cameraViewModel.insert(newData)
            Toast.makeText(this, "成功记录~", Toast.LENGTH_SHORT).show()
            finish()
        }



    }//OnCreate()




    private fun takePhoto() {
        val imageCapture = imageCapture ?: return
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Video.Media.RELATIVE_PATH, "Pictures/Focus/$missionName-$sysTimeStartStr")
            }
        }

        var mFileName = this.getExternalFilesDir("$missionName-$sysTimeStartStr").toString()
        mFileName = mFileName + "/${UUID.randomUUID()}.jpg"
        val file = File(mFileName)
        if (!file.exists()) {
            if (!file.mkdirs()){
                Log.e(TAG, "create directory failed.")
            }
        }
        //储存到Pictures
        val outputOptions1 = ImageCapture.OutputFileOptions
            .Builder(contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues)
            .build()
        //储存副本到app目录
        val outputOptions2 = ImageCapture.OutputFileOptions
            .Builder(file)
            .build()

        imageCapture.takePicture(
            outputOptions1,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun
                        onImageSaved(output: ImageCapture.OutputFileResults){
                    val msg = "Photo capture succeeded: ${output.savedUri}"
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)
                }
            }
        )
        imageCapture.takePicture(
            outputOptions2,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun
                        onImageSaved(output: ImageCapture.OutputFileResults){
                    val msg = "Photo capture succeeded: ${output.savedUri}"
                    Log.d(TAG, msg)
                }
            }
        )
    }

    private fun captureVideo() {
        val videoCapture = this.videoCapture ?: return

        binding.videoCaptureButton.isEnabled = false

        val curRecording = recording
        if (curRecording != null) {
            // Stop the current recording session.
            curRecording.stop()
            recording = null
            return
        }

        // create and start a new recording session
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Video.Media.RELATIVE_PATH, "Pictures/Focus/$missionName-$sysTimeStartStr")
            }
        }

        //储存到媒体
        val mediaStoreOutputOptions = MediaStoreOutputOptions
            .Builder(contentResolver, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            .setContentValues(contentValues)
            .build()

        recording = videoCapture.output
            .prepareRecording(this, mediaStoreOutputOptions)
            .apply {
                if (PermissionChecker.checkSelfPermission(this@CameraActivity,
                        Manifest.permission.RECORD_AUDIO) ==
                    PermissionChecker.PERMISSION_GRANTED)
                {
                    withAudioEnabled()
                }
            }
            .start(ContextCompat.getMainExecutor(this)) { recordEvent ->

                when(recordEvent) {
                    is VideoRecordEvent.Start -> {
                        binding.videoCaptureButton.apply {
                            binding.videoCaptureButton.setImageResource(R.drawable.ic_video_stop)
                            isEnabled = true
                        }
                    }
                    is VideoRecordEvent.Finalize -> {
                        if (!recordEvent.hasError()) {
                            val msg = "Video capture succeeded: " +
                                    "${recordEvent.outputResults.outputUri}"
                            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT)
                                .show()
                            Log.d(TAG, msg)
                        } else {
                            recording?.close()
                            recording = null
                            Log.e(TAG, "Video capture ends with error: " +
                                    "${recordEvent.error}")
                        }
                        binding.videoCaptureButton.apply {
                            binding.videoCaptureButton.setImageResource(R.drawable.ic_video_rec)
                            isEnabled = true
                        }
                    }
                }
            }
    }

    private fun startCamera(){
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }
            imageCapture = ImageCapture.Builder().build()
            //video
            val recorder = Recorder.Builder()
                .setQualitySelector(
                    QualitySelector.from(
                        Quality.HIGHEST,
                    FallbackStrategy.higherQualityOrLowerThan(Quality.SD)))
                .build()
            videoCapture = VideoCapture.withOutput(recorder)

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture, videoCapture)

            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    //点击对焦
    private fun showTapView(x: Int, y: Int) {
        val popupWindow = PopupWindow(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val imageView = ImageView(this)
        imageView.setImageResource(R.drawable.scan)
        popupWindow.contentView = imageView
        popupWindow.showAsDropDown(binding.viewFinder, x-60, y-60)//居中
        binding.viewFinder.postDelayed({ popupWindow.dismiss() }, 1000)
        binding.viewFinder.playSoundEffect(SoundEffectConstants.CLICK)
    }


    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        var lightflag:Boolean = true
        private var camera: Camera? =null
        private const val TAG = "CameraXApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }
}