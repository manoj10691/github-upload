package com.vportals.app.model

import com.google.gson.annotations.SerializedName

data class Ballot(
    val VoteID: Int,
    val VoteTypeID: Int,
    val VoteText: String,
    val MeetingDateTime: String,
    val Address: String,
    val UnitNumber: String,
    val HasVoted: Boolean,
    val IsRequireVotingCode: Boolean
)
