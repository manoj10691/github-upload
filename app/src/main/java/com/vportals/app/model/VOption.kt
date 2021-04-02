package com.vportals.app.model

import com.google.gson.annotations.SerializedName

data class VOption(
    val VoteOptionID: Int,
    val OptionText: String,
    val OptionText2: String,
    val FirstName: String,
    val LastName: String,
    val FullName: String,
    var Comment: String,
    val Sequence: Int,
    var Rating: Int,
    var HasSelected: Boolean,
    val optionAttachments: List<VOptionAttachments>
)
