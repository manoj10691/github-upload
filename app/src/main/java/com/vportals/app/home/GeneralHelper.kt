package com.vportals.app.home

import android.content.Context
import androidx.core.content.ContextCompat

fun Context.fetchColor(id : Int) : Int = ContextCompat.getColor(this,id)
