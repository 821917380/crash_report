package com.example.crashReport;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.util.Log;

import com.devin.core.network.result.ApiException;
import com.example.crashReport.bean.DaoSession;
import com.example.crashReport.bean.ExceptionBean;
import com.example.crashReport.bean.ExceptionBeanDao;
import com.example.crashReport.handler.CrashHandler;
import com.google.gson.JsonParseException;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import retrofit2.adapter.rxjava.HttpException;

/**
 * Created by xupeizuo on 2017/3/2.
 */

public final class AbnormalCollection {
    private AbnormalCollection() {

    }

    public static final String EXCEPTION_TYPE_NET_WORK="abnormalNetwork";
    public static final String EXCEPTION_TYPE_CRASH="abnormalCrash";
    public static final String EXCEPTION_TYPE_THROW_E="abnormalCatchException";
    public static final String EXCEPTION_TYPE_POSITION="abnormalPositioning";

    public String batteryHealth="未知";
    public static final String APP_CHANNEL="SMEC";

    public static final String submitStatus_READY="readyToUpLoad";
    public static final String submitStatus_ING="upLoading";
    public static final String submitStatus_OVER="over";

    private String api;
    private String token;

    private static AbnormalCollection abnormalCollection;
    private Context applicationContext = CrashHandler.getCrashHandler().getApplicationContext();
    private String appName= BuildConfig.APPLICATION_ID;
    private String appUser = CrashHandler.getCrashHandler().getAppUser();

    private TelephonyManager telephonyManager = (TelephonyManager) applicationContext.getSystemService(Context.TELEPHONY_SERVICE);
    private ExceptionBean exceptionBean;
    private DeviceMsg deviceMsg;

    public static synchronized AbnormalCollection getAbnormalCollection(){
        if(abnormalCollection == null){
            abnormalCollection = new AbnormalCollection();
        }

        return abnormalCollection;
    }

    /**
     * 保存异常信息
     * @param e
     * @param exceptionType
     */
    public synchronized void  saveExceptionMsg(Object e,String exceptionType){
        String [] strings=getExceptionStackTrace(e,exceptionType);
        if(exceptionBean == null ){
            exceptionBean=new ExceptionBean();
//            exceptionBean.setDeviceUuid(telephonyManager.getDeviceId());
            exceptionBean.setAppChannel(APP_CHANNEL);
            exceptionBean.setSubmitStatus(submitStatus_READY);
            exceptionBean.setAppName(appName);
        }
        exceptionBean.setAppUser(appUser);
        exceptionBean.setUuid(getUUID());
        exceptionBean.setExceptionCode(exceptionType);
        exceptionBean.setAppVersion(getPackageInfo(applicationContext).versionName);
        exceptionBean.setCreationDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        exceptionBean.setExceptionMsg(strings[0]);
        exceptionBean.setExceptionStack(strings[0]);
        exceptionBean.setIsDebug(BuildConfig.BUILD_TYPE.equals("debug") ? "YES" : "NO");
        exceptionBean.setDeviceInfo(getDevice(applicationContext));
        Log.e("$$$$****&&&&",exceptionBean.toString());

        saveException(exceptionBean);
    }

    /**
     * 获取异常信息的堆栈
     * @param ex
     * @param exceptionType
     * @return
     */
    private String[] getExceptionStackTrace(Object ex,String exceptionType){
        String[] s= new String[2];
        if(exceptionType.equals(EXCEPTION_TYPE_NET_WORK)){
            if(ex instanceof HttpException){
                HttpException httpException = (HttpException) ex;
                s[0]=httpException.code()+"";
                s[1]=httpException.getMessage();
            }else if(ex instanceof JsonParseException
                    || ex instanceof JSONException
                    || ex instanceof ParseException){
                s[0]="返回结果错误";
                s[1]="返回结果错误";
            }else if (ex instanceof ApiException){    //服务器返回的错误
                ApiException apiException = (ApiException) ex;
                s[0]=apiException.getHttpResult().getCode()+"";
                s[1]=apiException.getHttpResult().getDevMsg()+"";
            }else {
                s[0]="fail";
                s[1]="网络连接异常";
            }
        }else {
            Exception e=(Exception)ex;
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw, true));
            String strs = sw.toString();
            String[] arr=strs.split("\n\t");
            if(arr.length>=2){
                s[0]=arr[0].replaceAll("\n\t","");
                s[1]=arr[1].replaceAll("\n\t","");
            }
        }

        return s;
    }

    /**
     * 获取设备信息
     * @return
     */
    private String getDevice(Context applicationContext){
        String netWorkState="";

        if(networkConnected(applicationContext)){
            netWorkState="有网络链接";
        }else {
            netWorkState="无网络链接";
        }

        if(deviceMsg == null){
            deviceMsg=new DeviceMsg();
            deviceMsg.setModelType(android.os.Build.MODEL);
            deviceMsg.setAvailMemory(getAvailMemory(applicationContext));
        }

        deviceMsg.setBatteryHealth(batteryHealth);
        deviceMsg.setNetWorkState(netWorkState);

        return deviceMsg.toString();
    }

    /**
     * 获取android当前可用内存大小
     * @param context
     * @return
     */
    private String getAvailMemory(Context context) {//

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        return Formatter.formatFileSize(context, mi.availMem);// 将获取的内存大小规格化
    }

    /**
     * 手机总内存
     * @return
     */
    private String getTotalMemory(Context context) {
        String str1 = "/proc/meminfo";// 系统内存信息文件
        String str2;
        String[] arrayOfString;
        long initial_memory = 0;

        try {
            FileReader localFileReader = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(
                    localFileReader, 8192);
            str2 = localBufferedReader.readLine();// 读取meminfo第一行，系统总内存大小

            arrayOfString = str2.split("\\s+");
            for (String num : arrayOfString) {
                Log.i(str2, num + "\t");
            }

            initial_memory = Integer.valueOf(arrayOfString[1]).intValue() * 1024;// 获得系统总内存，单位是KB，乘以1024转换为Byte
            localBufferedReader.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return Formatter.formatFileSize(context, initial_memory);// Byte转换为KB或者MB，内存大小规格化
    }

    /**
     * 获取当前网络连接状况
     * @param context
     * @return
     */
    public static boolean networkConnected(Context context){

        if (context != null){
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = manager.getActiveNetworkInfo();
            if (info != null)
                return info.isAvailable();
        }

        return false;
    }

    /**
     * 添加
     * @param exceptionBean
     * @return
     */
    public boolean saveException(ExceptionBean exceptionBean) {
        boolean bl = false;

        try {
            DaoSession daoSession = CrashHandler.getCrashHandler().getDaoSession();
            ExceptionBeanDao exceptionBeanDao = daoSession.getExceptionBeanDao();
            exceptionBeanDao.insertOrReplace(exceptionBean);
            bl = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bl;
    }

    class DeviceMsg implements Serializable{

        public String modelType;
        public String netWorkState;
        public String batteryHealth;
        public String availMemory;

        public String getModelType() {
            return modelType;
        }

        public void setModelType(String modelType) {
            this.modelType = modelType;
        }

        public String getNetWorkState() {
            return netWorkState;
        }

        public void setNetWorkState(String netWorkState) {
            this.netWorkState = netWorkState;
        }

        public String getBatteryHealth() {
            return batteryHealth;
        }

        public void setBatteryHealth(String batteryHealth) {
            this.batteryHealth = batteryHealth;
        }

        public String getAvailMemory() {
            return availMemory;
        }

        public void setAvailMemory(String availMemory) {
            this.availMemory = availMemory;
        }

        @Override
        public String toString() {
            return "设备信息:" + "设备型号:" + modelType + ", 网络状况:" + netWorkState +", 电池状况:" + batteryHealth +", 可用内存:" + availMemory + '。';
        }
    }

    /**
     * 获取UUID
     * @return
     */
    private String getUUID() {
        UUID uuid = UUID.randomUUID();
        String str = uuid.toString();
        // 去掉"-"符号
        String temp = str.substring(0, 8) + str.substring(9, 13) + str.substring(14, 18) + str.substring(19, 23) + str.substring(24);
        return str+","+temp;
    }

    /**
     * 初始化包名信息
     * @param context
     * @return
     */
    private PackageInfo getPackageInfo(Context context) {
        PackageInfo pi = null;

        try {
            PackageManager pm = context.getPackageManager();
            pi = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);
            return pi;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pi;
    }

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

