package com.ddy.kotlin.core.services

import com.ddy.kotlin.core.dto.TranslationJob

interface TmsIntegrationService {

    fun createTranslationJob(job: TranslationJob): String
}