package com.turkcell.ticketapp.common

import android.content.Context
import androidx.annotation.StringRes

class StringProvider(
    private val context: Context
) {
    fun get(@StringRes resId: Int): String = context.getString(resId)
}
