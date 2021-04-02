package com.vportals.app.service

import android.content.Context
import com.vportals.app.service.RetrofitClient.getClient

object ApiUtils {

//    private const val baseUrl = "https://qcapi.associationportals.com/"
    private const val baseUrl = "https://api.votingportals.com/"
   // public const val baseCDNUrl = "https://qccdn.associationportals.com"
    public const val baseCDNUrl = "https://cdn.votingportals.com"

    fun getApiService(context: Context): ApiServiceInterface {
        return getClient(baseUrl, context)!!.create(ApiServiceInterface::class.java)
    }
}
