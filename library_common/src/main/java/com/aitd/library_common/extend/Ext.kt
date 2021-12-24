package com.aitd.library_common.extend

import android.content.Context
import androidx.annotation.BoolRes
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.IntegerRes
import androidx.core.content.ContextCompat

fun Context.getCompatColor(@ColorRes id: Int) = ContextCompat.getColor(this, id)

fun Context.getCompatDrawable(@DrawableRes id: Int) = ContextCompat.getDrawable(this, id)

fun Context.getInteger(@IntegerRes id: Int) = resources.getInteger(id)

fun Context.getBoolean(@BoolRes id: Int) = resources.getBoolean(id)