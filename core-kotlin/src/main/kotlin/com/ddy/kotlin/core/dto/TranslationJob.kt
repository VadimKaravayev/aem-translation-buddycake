package com.ddy.kotlin.core.dto

import com.adobe.granite.translation.api.TranslationMetadata
import com.adobe.granite.translation.api.TranslationState
import java.util.Date

data class TranslationJob(
    val name: String,
    val description: String,
    val sourceLanguage: String,
    val targetLanguage: String,
    val dueDate: Date?,
    val state: TranslationState,
    val jobMetadata: TranslationMetadata
)
