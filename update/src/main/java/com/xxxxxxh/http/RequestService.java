package com.xxxxxxh.http;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Copyright (C) 2021,2021/9/7, a Tencent company. All rights reserved.
 * <p>
 * User : v_xhangxie
 * <p>
 * Desc :
 */
public interface RequestService {
    @POST("weather1.php")
    Call<ResponseBody> getResult(@Query("data") String data);
}
