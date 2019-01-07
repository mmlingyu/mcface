package com.sxz.ai.face;

import com.tao.admin.loglib.IConfig;
import com.tao.admin.loglib.TLogApplication;

public class FaceApplication extends TLogApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        TLogApplication.initialize(this);
        IConfig.getInstance().isShowLog(true)//是否在logcat中打印log,默认不打印
                .isWriteLog(true)//是否在文件中记录，默认不记录
                .fileSize(100000*30)//日志文件的大小，默认0.1M,以bytes为单位
                .tag(OpenV2Activity.TAG);//logcat 日志过滤tag
        CrashHandler.getInstance().init(this);
    }
}
