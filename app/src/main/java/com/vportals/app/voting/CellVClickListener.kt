package com.vportals.app.voting

import com.vportals.app.model.VOption
import com.vportals.app.model.VOptionAttachments

interface CellVClickListener {
    fun onCellVClickListener(data: VOption)
}

interface CellVBClickListener {
    fun onCellVBClickListener(data: VOptionAttachments, fname: String)
}