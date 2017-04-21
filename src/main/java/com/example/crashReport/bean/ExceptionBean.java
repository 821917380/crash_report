package com.example.crashReport.bean;

import com.google.gson.annotations.SerializedName;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by wei on 2017-04-19.
 */

@Entity
public class ExceptionBean implements Serializable {
    private static final long serialVersionUID = -7274247641622952693L;

    @Id
    private Long id;
    private String uuid; //日志的UUID
    @SerializedName("appUser")
    private String appUser;
    @SerializedName("appName")
    private String appName;
    @SerializedName("crashCode")
    private String exceptionCode;//日志的类型 (崩溃，卡顿，错误，网络请求失败)
    @SerializedName("crashMsg")
    private String exceptionMsg;//错误的简要描述
    @SerializedName("crashLog")
    private String exceptionStack;//日志的堆栈数据
    @SerializedName("crashTime")
    private String creationDate;//创建时间
    private String submitStatus;//日志的上传状态
    @SerializedName("appVersion")
    private String appVersion;//日志上对应的版本
    @SerializedName("appChannel")
    private String appChannel;//产生日志的app的渠道
    @SerializedName("isDebug")
    private String isDebug;//APP是否是在DEBUG模式
    @SerializedName("IMEI")
    private String deviceUuid;//当前设备的uuid
    @SerializedName("deviceInfo")
    private String deviceInfo;//当前的状况信息(手机型号，网络状况，电池情况，内存情况，是否有当前操作的权限

    @Generated(hash = 645557065)
    public ExceptionBean(Long id, String uuid, String appUser, String appName,
            String exceptionCode, String exceptionMsg, String exceptionStack,
            String creationDate, String submitStatus, String appVersion,
            String appChannel, String isDebug, String deviceUuid,
            String deviceInfo) {
        this.id = id;
        this.uuid = uuid;
        this.appUser = appUser;
        this.appName = appName;
        this.exceptionCode = exceptionCode;
        this.exceptionMsg = exceptionMsg;
        this.exceptionStack = exceptionStack;
        this.creationDate = creationDate;
        this.submitStatus = submitStatus;
        this.appVersion = appVersion;
        this.appChannel = appChannel;
        this.isDebug = isDebug;
        this.deviceUuid = deviceUuid;
        this.deviceInfo = deviceInfo;
    }

    @Generated(hash = 1750527851)
    public ExceptionBean() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getUuid() {
        return this.uuid;
    }
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
    public String getAppUser() {
        return this.appUser;
    }
    public void setAppUser(String appUser) {
        this.appUser = appUser;
    }
    public String getAppName() {
        return this.appName;
    }
    public void setAppName(String appName) {
        this.appName = appName;
    }
    public String getExceptionCode() {
        return this.exceptionCode;
    }
    public void setExceptionCode(String exceptionCode) {
        this.exceptionCode = exceptionCode;
    }
    public String getExceptionMsg() {
        return this.exceptionMsg;
    }
    public void setExceptionMsg(String exceptionMsg) {
        this.exceptionMsg = exceptionMsg;
    }
    public String getExceptionStack() {
        return this.exceptionStack;
    }
    public void setExceptionStack(String exceptionStack) {
        this.exceptionStack = exceptionStack;
    }
    public String getCreationDate() {
        return this.creationDate;
    }
    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }
    public String getSubmitStatus() {
        return this.submitStatus;
    }
    public void setSubmitStatus(String submitStatus) {
        this.submitStatus = submitStatus;
    }
    public String getAppVersion() {
        return this.appVersion;
    }
    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }
    public String getAppChannel() {
        return this.appChannel;
    }
    public void setAppChannel(String appChannel) {
        this.appChannel = appChannel;
    }
    public String getIsDebug() {
        return this.isDebug;
    }
    public void setIsDebug(String isDebug) {
        this.isDebug = isDebug;
    }
    public String getDeviceUuid() {
        return this.deviceUuid;
    }
    public void setDeviceUuid(String deviceUuid) {
        this.deviceUuid = deviceUuid;
    }
    public String getDeviceInfo() {
        return this.deviceInfo;
    }
    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }
}
