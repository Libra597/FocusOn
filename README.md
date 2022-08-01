# FocusOn

一、整体架构
专注助手（Focus on）由Jyq独立开发，采用MVVM架构模式，

利用Android CameraX实现相机模块，将数据库封装于底层Repository，

利用ViewModel提供和处理数据。App遵循Activity -> Fragment两层架构，

使用Jetpack导航组件实现界面切换。


二、App组成
App分为三个包，Camera包含CameraActivity及其Viewmodel；

database包含数据、Dao、repository、Database；

ui包含以导航控制的多Fragment界面。



三、App功能

1. 专注功能
App以Camera帮助用户专注为目的，首页使用醒目取景框，以及下方轮播励志标语，提醒用户尽快进入专注，完成任务，

用户输入任务名称进入专注后，打开Camera界面，用户可在此进行拍照、录像、切换摄像头、打开闪光灯、点击对焦等操作，

进入此界面后，即开始专注，用户可点击隐藏按钮，进入沉浸式状态，界面上方显示当前时间以及已专注时间，方便用户查看，

用户点击完成后自动记录到数据库。



![1.jpg](https://s2.loli.net/2022/08/01/gWo3F5HxrQIwlYC.jpg)           ![8.jpg](https://s2.loli.net/2022/08/01/rh4NkdVXm8ysaDg.jpg)           



2. 记录&相册功能
完成专注任务后，计入数据库并显示到列表，每个item可编辑图标、感想、总结等内容，

item内部可将此次任务的照片/录像读取为相册，点击图片利用系统相册打开/编辑。




![2.jpg](https://s2.loli.net/2022/08/01/hNkAM1ufWZDx6y3.jpg)   ![6.jpg](https://s2.loli.net/2022/08/01/fmwxvJijdzkETbh.jpg)     ![7.jpg](https://s2.loli.net/2022/08/01/wunExKiUh89Lzbl.jpg)




3. 个人&App设置
设置界面可编辑个人头像、昵称、个性签名，可设置目标和已完成，并在progressBar中显示完成进度，

提供了5种主题，用户可选择自己喜欢的主题。



![3.jpg](https://s2.loli.net/2022/08/01/APjonDHRN4U39tS.jpg)     ![4.jpg](https://s2.loli.net/2022/08/01/mFqX9pl1M6GPNuc.jpg)     ![5.jpg](https://s2.loli.net/2022/08/01/Z2FLePC7sEIKq96.jpg)







最后祝愿看到此App的帅锅/美女：身体倍倍棒，事业节节高，生活嘎嘎顺，开心又美好~
