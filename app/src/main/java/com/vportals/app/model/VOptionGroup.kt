package com.vportals.app.model

import com.google.gson.annotations.SerializedName

data class VOptionGroup(
    val VoteOptionGroupID: Int,
    val GroupName: String,
    val MaxSelectionCount: Int,
    val MinSelectionCount: Int,
    val GroupType: String,
    val voteOptions: List<VOption>
)
