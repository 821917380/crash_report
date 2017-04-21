package com.example.crashReport;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.example.crashReport.bean.DaoMaster;
import com.example.crashReport.bean.DaoSession;
import com.example.crashReport.bean.ExceptionBean;
import com.example.crashReport.bean.ExceptionBeanDao;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.example.crashReport.test", appContext.getPackageName());
    }

    @Test
    public void testAdd() {
        Context appContext = InstrumentationRegistry.getContext();
        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(appContext, "crash_report.db", null);
        DaoMaster daoMaster = new DaoMaster(devOpenHelper.getWritableDb());
        DaoSession daoSession = daoMaster.newSession();
        ExceptionBeanDao exceptionBeanDao = daoSession.getExceptionBeanDao();

        ExceptionBean exceptionBean = new ExceptionBean();
        exceptionBean.setId(null);
        exceptionBean.setAppName("appName");
        exceptionBean.setAppUser("appUser");

        exceptionBeanDao.insert(exceptionBean);

        List<ExceptionBean> exceptionBeanList = exceptionBeanDao.loadAll();
        for (ExceptionBean exceptionBean1 : exceptionBeanList) {
            Log.i("testAdd", exceptionBean1.getId() + ", " + exceptionBean.getAppName());
        }
    }
}
