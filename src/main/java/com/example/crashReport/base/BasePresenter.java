package com.example.crashReport.base;

import android.content.Context;

import com.devin.core.network.result.ApiException;
import com.devin.core.network.result.HttpResult;

import java.io.Serializable;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * Created by wei on 2017-02-22.
 */

public abstract class BasePresenter implements Serializable {
    protected CompositeSubscription mSubscriptions;
    protected Context mContext;

    private BasePresenter() {
        mSubscriptions = new CompositeSubscription();
    }

    public BasePresenter(Context context) {
        this();
        mContext = context;
    }

    public abstract void subscribe();

    public void unsubscribe() {
        mSubscriptions.clear();
    }

    protected <T> Observable<T> initExceptionHandler(Observable<HttpResult<T>> observable) {

        return observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<HttpResult<T>, T>() {
                    @Override
                    public T call(HttpResult<T> httpResult) {
                        if ("SUCCESS".equals(httpResult.getCode())){
                            return httpResult.getData();
                        } else {
                            if("TOKEN_TIME_OUT".equals(httpResult.getDevMsg())){
                                Timber.e(httpResult.getDevMsg());
//                                IntentRouters.open(TechnicalNoticeApplication.getContext(),"mzule://userLogin/true");
                            }
                            throw new ApiException(httpResult);
                        }
                    }
                });
    }
}

