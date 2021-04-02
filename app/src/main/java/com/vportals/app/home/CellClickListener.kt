package com.vportals.app.home

import com.vportals.app.model.Ballot

interface CellClickListener {
    fun onCellClickListener(data: Ballot)
}