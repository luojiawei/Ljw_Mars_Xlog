# Mars_Xlog
### 一、mars简介
mars 是微信官方的终端基础组件，是一个使用 C++ 编写的业务性无关，平台性无关的基础组件。 目前已接入微信 Android、iOS、Mac、Windows、WP 等客户端。

### 二、xlog简介
xlog是mars系统中可以独立使用的日志模块。

### 三、xlog优点

xlog相比其它日志模块，有如下优点：
1. 高性能高压缩率。
2. 不丢失任何一行日志。
3. 避免系统卡顿。
4. 避免CPU波峰。

### 四、编译xlog
先下载mars-master.zip

github地址 https://github.com/Tencent/mars

进入其中的libraries目录，直接执行下面的Python脚本：


```
python build_android.py
```
![image](http://note.youdao.com/yws/public/resource/3f60271d6e59c5c75fdb0da28af28425/xmlnote/802301E8E2A743818806CA850A26B59B/2330)

<font color=red>运行时提示python版本必须为2.7xxx</font>

![image](http://note.youdao.com/yws/public/resource/3f60271d6e59c5c75fdb0da28af28425/xmlnote/92DC60725D694F85BCD7A88C5BAC656A/2333)

<font color=red>运行时提示ndk版本要大于r11c</font>

于是下载最新的android-ndk-r14b-windows-x86_64了运行
![image](http://note.youdao.com/yws/public/resource/3f60271d6e59c5c75fdb0da28af28425/xmlnote/16F0FB3C73C64408A6FE2F48C853F22C/2331)

看见选择菜单了，选择3，编译xlog动态库

可是，又报错了
![image](http://note.youdao.com/yws/public/resource/3f60271d6e59c5c75fdb0da28af28425/xmlnote/DEDAF47A0BF74DA4A1369D1514DAAD2F/2332)

搜索mars github官方资料，有人遇到同样的问题，建议使用ndk11c
https://github.com/Tencent/mars/issues/307

使用ndk11c后，终于编译成功了！！！

mars_xlog_sdk\src目录下生成所需的java文件

mars_xlog_sdk\libs\armeabi-v7a目录生成所需的libmarsxlog.so和libstlport_shared.so库文件


### 五、使用xlog库和java文件

将java文件和so分别拷贝至工程目录。

![image](http://note.youdao.com/yws/public/resource/3f60271d6e59c5c75fdb0da28af28425/xmlnote/207C79136F264689A4A23E421AAEF111/2325)

因为要往TF卡写文件，AndroidManifest.xml中加入写权限

```
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
```
初始化 xlog

```Java
private void initMarsXlog() {
    // 加载so
    System.loadLibrary("stlport_shared");
    System.loadLibrary("marsxlog");

    if (BuildConfig.DEBUG) {
        //appenderOpen(int level, int mode, String cacheDir, String logDir, String nameprefix, String pubkey)
        Xlog.appenderOpen(Xlog.LEVEL_DEBUG, Xlog.AppednerModeAsync, cachePath, logPath, "MarsXlogDemo", "");
        Xlog.setConsoleLogOpen(true);

    } else {
        Xlog.appenderOpen(Xlog.LEVEL_INFO, Xlog.AppednerModeAsync, cachePath, logPath, "MarsXlogDemo", "");
        Xlog.setConsoleLogOpen(false);
    }

    Log.setLogImp(new Xlog());
}
```
使用xlog,点击测试按钮，写log

```Java
@Override
public void onClick(View view) {
    if (mButton.getId() == view.getId()) {
        // 测试，写log
        Log.d("test","write log !!!");
    }
}
```
退出时调用停止

```Java
protected void onDestroy() {
    super.onDestroy();
    //停止Log记录
    com.tencent.mars.xlog.Log.appenderClose();
}
```
### 六、解密log
log写入到/sdcard/mars/log/目录

导入log：

```
adb pull /sdcard/mars/log/MarsXlogDemo_20171109.xlog "E:\mars\log\crypt"
```
把log导出至Mars源码log/crypt/这个文件夹

执行脚本（decode_mars_nocrypt_log_file.py在log/crypt目录）


```
python decode_mars_nocrypt_log_file.py
```
当前目录下就会生成解密后的MarsXlogDemo_20171109.xlog.log

### 七、参考资料
https://github.com/Tencent/mars

http://blog.csdn.net/eclipsexys/article/details/53965065

https://mp.weixin.qq.com/s/cnhuEodJGIbdodh0IxNeXQ

http://blog.csdn.net/tencent_bugly/article/details/53157830

### OVER