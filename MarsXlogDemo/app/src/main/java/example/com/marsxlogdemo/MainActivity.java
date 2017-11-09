package example.com.marsxlogdemo;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.tencent.mars.xlog.Log;
import com.tencent.mars.xlog.Xlog;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE" };

    final String sdcardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    final String logPath = sdcardPath + "/mars/log";
    final String cachePath = sdcardPath + "/mars/cachelog";

    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButton = (Button)findViewById(R.id.button);
        mButton.setOnClickListener(this);

        // 申请权限
        verifyStoragePermissions(this);

        //初始化 xlog
        initMarsXlog();
    }

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

    @Override
    public void onClick(View view) {
        if (mButton.getId() == view.getId()) {
            // 测试，写log
            Log.d("test","write log !!!");
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        //停止Log记录
        com.tencent.mars.xlog.Log.appenderClose();
    }

    public static void verifyStoragePermissions(Activity activity) {
        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,"android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}