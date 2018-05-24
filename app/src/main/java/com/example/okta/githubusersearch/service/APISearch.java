package com.example.okta.githubusersearch.service;

import com.example.okta.githubusersearch.model.ResponseSearch;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface APISearch {
    @GET("/search/users")
    Call<ResponseSearch> GetUsers(@Query("q") String username,
                                  @Query("page") String page);
}
