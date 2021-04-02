package com.vportals.app.model

data class BallotResponse(
    val Flag: Boolean,
    val Message: String,
    val StatusCode: Int,
    val Data: List<Ballot>
)