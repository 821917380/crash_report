package com.example.crashReport.handler;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.devin.core.network.OKHttpClientFactory;
import com.example.crashReport.AbnormalCollection;
import com.example.crashReport.bean.DaoMaster;
import com.example.crashReport.bean.DaoSession;
import com.example.crashReport.network.RetrofitLogServiceManager;
import com.example.crashReport.network.TokenInterceptor;
import com.example.crashReport.service.CrashService;
import com.tencent.bugly.crashreport.CrashReport;

import okhttp3.OkHttpClient;

/**
 * Created by xupeizuo on 2017/3/3.
 */

public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private CrashHandler() {
    }

    private static final String TAG = CrashHandler.class.getSimpleName();

    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private static CrashHandler crashHandler;
    private DaoSession daoSession;
    private Context mContext;
    private String appUser;

    public static synchronized CrashHandler getCrashHandler(){
        if(crashHandler == null){
            crashHandler = new CrashHandler();
        }
        return crashHandler;
    }

    /**
     * 暴露出的初始化方法
     * @param applicationContext
     * @param appUser
     * @param token
     * @param baseUrl
     * @param api
     * @param DBName
     */
    public void init(@NonNull Context applicationContext, String appUser, String token, String baseUrl, String api, String DBName){
        initInfo(applicationContext, appUser, token, baseUrl, api, DBName);

        // 启动Service
        Intent crashServiceIntent = new Intent(applicationContext, CrashService.class);
        applicationContext.startService(crashServiceIntent);
    }

    /**
     * 暴露出的初始化方法
     * @param applicationContext
     * @param appUser
     * @param token
     * @param baseUrl
     * @param api
     * @param DBName
     * @param firstMilliseconds
     * @param nextMilliseconds
     */
    public void init(@NonNull Context applicationContext, String appUser, String token, String baseUrl, String api, String DBName, long firstMilliseconds, long nextMilliseconds) {
        initInfo(applicationContext, appUser, token, baseUrl, api, DBName);

        // 启动Service
        Intent crashServiceIntent = new Intent(applicationContext, CrashService.class);
        crashServiceIntent.putExtra("firstMilliseconds", firstMilliseconds);
        crashServiceIntent.putExtra("nextMilliseconds", nextMilliseconds);
        applicationContext.startService(crashServiceIntent);
    }

    /**
     * 初始化基本信息
     * @param applicationContext
     * @param appUser
     * @param baseUrl
     * @param api
     * @param DBName
     */
    private void initInfo(Context applicationContext, String appUser, String token, String baseUrl,  String api, String DBName) {
        this.mContext = applicationContext;
        this.appUser = appUser;
        AbnormalCollection.getAbnormalCollection().setApi(api);
        AbnormalCollection.getAbnormalCollection().setToken(token);

        // 获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        // 设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
        // 初始化数据库
        initDB(applicationContext, DBName);

        // 初始化网络管理
        initRetrofitManager(applicationContext, baseUrl);
    }

    /**
     * 初始化网络管理
     * @param applicationContext
     * @param baseUrl
     */
    private void initRetrofitManager(Context applicationContext, String baseUrl) {
        OkHttpClient okHttpClient = OKHttpClientFactory.defaultOkHttpClient(applicationContext);
        okHttpClient = okHttpClient.newBuilder().addInterceptor(new TokenInterceptor()).build();
        RetrofitLogServiceManager.init(applicationContext, baseUrl, okHttpClient);
    }

    /**
     * 初始化Bugly
     * @param applicationContext
     * @param appId
     * @param isDebug
     * @param isCloseBugly
     */
    public void initBugly(@NonNull Context applicationContext, String appId, boolean isDebug, boolean isCloseBugly) {
        if (isCloseBugly) {
            CrashReport.closeBugly();
        } else {
            CrashReport.initCrashReport(applicationContext, appId, isDebug);
        }
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {

        if(ex == null){
            return;
        }

        ex.printStackTrace();
        AbnormalCollection.getAbnormalCollection().saveExceptionMsg(ex,AbnormalCollection.EXCEPTION_TYPE_CRASH);

        mDefaultHandler.uncaughtException(thread,ex);

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Looper.prepare();
//                Toast.makeText(mContext, "很抱歉,程序出现异常,即将退出", Toast.LENGTH_LONG)
//                        .show();
//                Looper.loop();
//            }
//        }).start();
//        try {
//            Thread.sleep(3000);
//        }catch (Exception e){
//            Log.e(TAG,"我是异常");
//            AbnormalCollection.getAbnormalCollection().saveExceptionMsg(e,AbnormalCollection.EXCEPTION_TYPE_THROW_E);
//        }
//        android.os.Process.killProcess(android.os.Process.myPid());
//        System.exit(1);
    }

    private void initDB(Context applicationContext, String DBName) {
        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(applicationContext, DBName, null);
        DaoMaster daoMaster = new DaoMaster(devOpenHelper.getWritableDb());
        daoSession = daoMaster.newSession();
    }

    public Context getApplicationContext() {
        return mContext;
    }

    public String getAppUser() {
        return appUser;
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }
}
