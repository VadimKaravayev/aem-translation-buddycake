package com.ddy.kotlin.core.utils

import com.fasterxml.jackson.databind.ObjectMapper

object JsonUtils {
    val mapper: ObjectMapper by lazy { ObjectMapper() }
}