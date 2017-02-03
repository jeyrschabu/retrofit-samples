package com.jeyrs.playground.retrofit;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Path;

import java.util.Map;

public interface JobService {
  @POST("/jobs/{name}")
  Call<Map> submit(@Path("name") String name);
}
