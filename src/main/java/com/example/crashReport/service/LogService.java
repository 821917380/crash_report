package com.example.crashReport.service;

import com.devin.core.network.result.HttpResult;
import com.example.crashReport.AbnormalCollection;
import com.example.crashReport.bean.ExceptionDto;

import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by wei on 2017-04-20.
 */

public interface LogService {
    /**
     * 上传异常信息
     * @param
     * @return
     */
    @POST("{api}")
    @Headers({"Accept: application/vnd.githuSampleb.v3.full+json"})
    Observable<HttpResult<Object>> sendExceptionMsgToServer(@Path("api") String api, @Body ExceptionDto exceptionDto);
}
