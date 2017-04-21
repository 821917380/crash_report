package com.example.crashReport.presenter;

import android.content.Context;
import android.util.Log;

import com.devin.core.network.result.ApiException;
import com.devin.core.network.result.HttpResult;
import com.devin.core.network.result.RetrofitException;
import com.example.crashReport.AbnormalCollection;
import com.example.crashReport.base.BasePresenter;
import com.example.crashReport.bean.ExceptionBean;
import com.example.crashReport.bean.ExceptionBeanDao;
import com.example.crashReport.bean.ExceptionDto;
import com.example.crashReport.handler.CrashHandler;
import com.devin.core.network.subscribe.APISubcribe;
import com.example.crashReport.network.RetrofitLogServiceManager;
import com.example.crashReport.service.CrashService;
import com.example.crashReport.service.LogService;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by wei on 2017-04-20.
 */

public class CrashServicePresenter extends BasePresenter implements CrashService.ExceptionMsgOperator {
    private LogService logService;
    public CrashServicePresenter(Context context) {
        super(context);
        logService = RetrofitLogServiceManager.getLogSericeInterface();
    }

    @Override
    public void uploadDataToServer(String api, final ExceptionDto exceptionDto) {
        Log.e("当前上传操作线程是",Thread.currentThread().getName());

        if(exceptionDto == null || !notEmpty(exceptionDto.getCrashLog())){
            return;
        }
        Observable<HttpResult<Object>> observable= logService.sendExceptionMsgToServer(api, exceptionDto);

        observable.subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).
                subscribe(new APISubcribe<HttpResult<Object>>() {
                    @Override
                    protected void onError(RetrofitException ex) {
                        changeStatus(exceptionDto);
                    }

                    @Override
                    protected void onResultError(ApiException ex) {
                        changeStatus(exceptionDto);
                    }

                    @Override
                    public void onNext(HttpResult<Object> objectHttpResult) {
                        if(objectHttpResult.getCode().equals("SUCCESS")){
                            deleteDataFromDB(exceptionDto);
                        }else {
                            changeStatus(exceptionDto);
                        }
                    }
                });
    }

    @Override
    public ExceptionDto getDataFromDB() {
        ExceptionDto exceptionDto=new ExceptionDto();
        ExceptionBeanDao exceptionBeanDao = CrashHandler.getCrashHandler().getDaoSession().getExceptionBeanDao();
        List<ExceptionBean> queryList = exceptionBeanDao.loadAll();

        if(notEmpty(queryList)){
            ArrayList<ExceptionBean> exceptionBeanArrayList=new ArrayList<>();
            for (ExceptionBean exceptionBean : queryList) {
                exceptionBean.setSubmitStatus(AbnormalCollection.submitStatus_ING);
                exceptionBeanArrayList.add(exceptionBean);
            }
            exceptionDto.setCrashLog(exceptionBeanArrayList);
            exceptionBeanDao.insertOrReplaceInTx(exceptionBeanArrayList);
        }
        return exceptionDto;
    }

    @Override
    public void deleteDataFromDB(ExceptionDto exceptionDto) {
        deleteObject(exceptionDto);
    }

    @Override
    public void subscribe() {

    }

    /**
     * 网络异常时 即日志上传失败时将日志状态改为 submitStatus_READY
     * @param exceptionDto
     */
    public void changeStatus(ExceptionDto exceptionDto){
        try {
            Log.e("即日志上传失败时将日志状态改为","");
            ArrayList<ExceptionBean> list=exceptionDto.getCrashLog();
            if(notEmpty(list)){
                for(ExceptionBean e1: list){
                    e1.setSubmitStatus(AbnormalCollection.submitStatus_READY);
                }

                CrashHandler.getCrashHandler().getDaoSession().getExceptionBeanDao().insertOrReplaceInTx(list);
            }
        }catch (Exception e){
            e.printStackTrace();
            AbnormalCollection.getAbnormalCollection().saveExceptionMsg(e,AbnormalCollection.EXCEPTION_TYPE_THROW_E);
        }
    }

    /**
     * 删除日志状态为上传完成的realm数据
     * @param exceptionDto
     * @return
     */
    public boolean deleteObject(ExceptionDto exceptionDto){

        try {
            ArrayList<ExceptionBean> list=exceptionDto.getCrashLog();
            if(notEmpty(list)){
                for(ExceptionBean e1: list){
                    e1.setSubmitStatus(AbnormalCollection.submitStatus_OVER);
                }
                ExceptionBeanDao exceptionBeanDao = CrashHandler.getCrashHandler().getDaoSession().getExceptionBeanDao();
                exceptionBeanDao.insertOrReplaceInTx(list);

                List<ExceptionBean> queryOver = exceptionBeanDao.queryBuilder().where(ExceptionBeanDao.Properties.SubmitStatus.eq(AbnormalCollection.submitStatus_OVER)).list();
                exceptionBeanDao.deleteInTx(queryOver);
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
            AbnormalCollection.getAbnormalCollection().saveExceptionMsg(e,AbnormalCollection.EXCEPTION_TYPE_THROW_E);
            return false;
        }
    }

    private boolean notEmpty(List<?> list){
        return list!=null&&!list.isEmpty();
    }
}
