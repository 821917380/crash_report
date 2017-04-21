package com.example.crashReport.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by xupeizuo on 2017/3/2.
 */

public class ExceptionDto implements Serializable {

    private ArrayList<ExceptionBean> crashLog;

    public ArrayList<ExceptionBean> getCrashLog() {
        return crashLog;
    }

    public void setCrashLog(ArrayList<ExceptionBean> crashLog) {
        this.crashLog = crashLog;
    }

    @Override
    public String toString() {
        return "ExceptionDto{" +
                "crashLog=" + crashLog +
                '}';
    }
}
