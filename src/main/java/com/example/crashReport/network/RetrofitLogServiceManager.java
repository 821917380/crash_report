package com.example.crashReport.network;

import android.content.Context;

import com.devin.core.network.OKHttpClientFactory;
import com.example.crashReport.service.LogService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscription;

/**
 * Created by xupeizuo on 2017/3/7.
 */

public final class RetrofitLogServiceManager {

    private static Retrofit retrofit ;

    public static Map<String, ArrayList<Subscription>> subscriptionMap ;

    public static void init(Context context, String baseUrl, OkHttpClient okHttpClient, Converter.Factory... converterFactorys) {
        retrofit = getRetrofit(context,baseUrl,okHttpClient,converterFactorys);
        subscriptionMap = new HashMap<>();
    }

    private static LogService logSericeInterface;
    public static LogService getLogSericeInterface(){
        if(logSericeInterface == null){
            logSericeInterface = retrofit.create(LogService.class);
        }
        return logSericeInterface;
    }

    /**
     * 初始化Retrofit，默认使用GsonConverterFactory和RxJavaCallAdapterFactory
     * @param context
     * @param baseUrl
     * @param converterFactorys
     * @return
     */
    private static Retrofit getRetrofit(Context context, String baseUrl, OkHttpClient okHttpClient, Converter.Factory... converterFactorys){

        if (converterFactorys == null || converterFactorys.length == 0) {
            converterFactorys = new Converter.Factory[1];
            converterFactorys[0] = GsonConverterFactory.create();
        }

        if(okHttpClient == null) {
            okHttpClient = OKHttpClientFactory.defaultOkHttpClient(context);
        }

        Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create());

        for(Converter.Factory converterFactory : converterFactorys) {
            retrofitBuilder.addConverterFactory(converterFactory);
        }

        return retrofitBuilder.build();
    }
}
