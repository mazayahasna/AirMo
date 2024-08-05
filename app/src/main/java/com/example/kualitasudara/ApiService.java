package com.example.kualitasudara;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    @POST("register-token")
    Call<ResponseBody> registerToken(@Body TokenRequest tokenRequest);
}
