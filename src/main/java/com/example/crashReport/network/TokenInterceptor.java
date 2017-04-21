package com.example.crashReport.network;

import com.example.crashReport.AbnormalCollection;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import timber.log.Timber;

/**
 * Created by apple on 2016/11/16.
 */

public class TokenInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        String token = AbnormalCollection.getAbnormalCollection().getToken();
        Timber.e(token == null ? "当前没有取到TOKEN\n":"\n");
        Request request = chain.request().newBuilder()
                .addHeader("DF_KEY", token == null ? "" : token)
                .build();
//        Request request = chain.request().newBuilder()
//                .addHeader("DF_KEY", "srQMsbaZKDstAWnHeakWzEnJCzHieChK")
//                .build();
        Response response = null ;
        try {
            response = chain.proceed(request);
        } catch (Exception e) {
            throw e ;
        }
        return response;
    }
}
