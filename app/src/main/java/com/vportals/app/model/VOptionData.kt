package com.vportals.app.model

data class VOptionData(
    val VotingToken: String,
    val VoteID: Int,
    val VoteTypeID: Int,
    val VoteText: String,
    val Instructions: List<VOptionInstructions>,
    val MeetingDateTime: String,
    val IsOptionsSorted: Boolean,
    val IsShowConfirmationWindow: Boolean,
    val IsSignatureAllowed: Boolean,
    val NameFormatID: Int,
    val voteOptionGroups: List<VOptionGroup>

)
