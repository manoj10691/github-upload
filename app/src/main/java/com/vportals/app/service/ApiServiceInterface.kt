package com.vportals.app.service

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.vportals.app.model.*
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.*

interface ApiServiceInterface {

    @POST("api/Login/LoginAuthentication")
    fun login(@Body logInRequest: RequestBody): Call<JsonObject>

    @Headers(
        "DeviceToken: 52.170.151.93",
        "Content-Type: application/x-www-form-urlencoded"
    )
    @GET("api/Login/dashboard")
    fun dashboard(@Header("AuthToken") dtoken: String): Call<JsonObject>

    @Headers(
        "DeviceToken: 52.170.151.93",
        "Content-Type: application/x-www-form-urlencoded"
    )
    @POST("api/Login/LogOut")
    fun logout(@Header("AuthToken") token: String): Call<JsonObject>

    @Headers("deviceID:13456;")
    @POST("api/Login/ForgotPassword")
    fun reset(@Body logInRequest: RequestBody): Call<JsonObject>

    @Headers(
        "DeviceToken: 52.170.151.93",
        "Content-Type: application/x-www-form-urlencoded"
    )
    @POST("api/Ballot/List")
    fun getVotingData(@Header("AuthToken") token: String): Call<BallotResponse>

    @Headers(
        "DeviceToken: 52.170.151.93",
        "Content-Type: application/x-www-form-urlencoded"
    )
    @POST("api/Ballot/GenerateVotingToken")
    fun enerateVT(@Header("AuthToken") token: String, @Body logInRequest: RequestBody): Call<JsonObject>

    @Headers(
        "DeviceToken: 52.170.151.93",
        "Content-Type: application/x-www-form-urlencoded"
    )
    @POST("api/Ballot/VoteOptions")
    fun getVotingOption(@Header("AuthToken") dtoken: String, @Header("VotingToken") vtoken: String, @Body logInRequest: RequestBody): Call<VOptionResponse>

    @Headers(
        "DeviceToken: 52.170.151.93",
        "Content-Type: application/json"
    )
    @POST("api/Ballot/Votesubmit")
    fun submitVote(@Header("AuthToken") atoken: String, @Header("VotingToken") vtoken: String, @Body voteRequest: String): Call<JsonObject>
}
