package com.xxxxxxh.update;

import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Copyright (C) 2021,2021/9/7, a Tencent company. All rights reserved.
 * <p>
 * User : v_xhangxie
 * <p>
 * Desc :
 */
public interface ResponseListener {
 void onResponse(Response<ResponseBody> response);
}
