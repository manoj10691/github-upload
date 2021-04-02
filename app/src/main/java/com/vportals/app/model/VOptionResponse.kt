package com.vportals.app.model

data class VOptionResponse(
    val Flag: Boolean,
    val Message: String,
    val StatusCode: Int,
    val Data: VOptionData
)