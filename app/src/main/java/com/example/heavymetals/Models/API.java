package com.example.heavymetals.Models;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface API {
    @FormUrlEncoded
    @POST("register_user.php")
    Call<RegisterResponse> registerUser(
            @Field("first_name") String firstName,
            @Field("last_name") String lastName,
            @Field("email") String email,
            @Field("password") String password,
            @Field("passwordConfirmation") String passwordConfirmation
    );
    @POST("forget_password.php")
    @FormUrlEncoded
    Call<ResetResponse> requestPasswordReset(
            @Field("email") String email
    );

}
