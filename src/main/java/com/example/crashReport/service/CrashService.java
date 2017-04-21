package com.example.crashReport.service;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.crashReport.AbnormalCollection;
import com.example.crashReport.bean.ExceptionBean;
import com.example.crashReport.bean.ExceptionDto;
import com.example.crashReport.presenter.CrashServicePresenter;
import com.example.crashReport.util.ThreadPoolExecutorUtil;

import java.util.List;

/**
 * Created by wei on 2017-04-19.
 */

public class CrashService extends Service {
    private CrashServicePresenter crashServicePresenter;
    private Handler handler;
    private long firstMilliseconds = 10000;
    private long nextMilliseconds = 600000;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            firstMilliseconds = intent.getLongExtra("firstMilliseconds", firstMilliseconds);
            nextMilliseconds = intent.getLongExtra("nextMilliseconds", nextMilliseconds);

            startUploadCrashReport(0);
        }

        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        if (handler != null) {
            handler.removeCallbacks(runnable);
        }
        super.onDestroy();
    }

    // 开始上传crash
    public void startUploadCrashReport(int timeInterval) {
        handler = new Handler();
        handler.postDelayed(runnable, timeInterval == 0 ? firstMilliseconds : nextMilliseconds);
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            startUploadCrashReport(1);
            operatorExceptionMsg();
        }
    };

    public void operatorExceptionMsg(){

        if(crashServicePresenter == null ){
            crashServicePresenter = getPresenter();
        }

        Log.e("*****","哈哈哈我现在不是广播");

        final ExceptionDto exceptionDto = getDataFromDB();

        if(exceptionDto !=null){
            ThreadPoolExecutorUtil.getFixedThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    uploadDataToServer(AbnormalCollection.getAbnormalCollection().getApi(), exceptionDto);
                }
            });
        }else {
            Log.e("&&&&","现在没有异常信息");
        }

    }

    /**
     * 上传数据至服务器
     * @param exceptionDto
     */
    public void uploadDataToServer(String api, ExceptionDto exceptionDto){
        crashServicePresenter.uploadDataToServer(api, exceptionDto);
    }

    /**
     * 获取本地异常信息
     * @return
     */
    public ExceptionDto getDataFromDB(){
        return crashServicePresenter.getDataFromDB();
    }

    public interface ExceptionMsgOperator{

        void uploadDataToServer(String api, ExceptionDto exceptionDto);

        ExceptionDto getDataFromDB();

        void deleteDataFromDB(ExceptionDto exceptionDto);
    }

    public CrashServicePresenter getPresenter(){
        return new CrashServicePresenter(this);
    }
}
