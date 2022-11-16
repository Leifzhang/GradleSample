package com.kronos.plugin

import java.text.SimpleDateFormat
import java.util.*

/**
 *
 *  @Author LiABao
 *  @Since 2022/10/11
 *
 */


fun time(): String? {
    val date = Date()
    return simpleDateFormat.format(date)
}

private val simpleDateFormat = SimpleDateFormat("yyyy/MM-dd/HH:mm:ss", Locale.CHINA)