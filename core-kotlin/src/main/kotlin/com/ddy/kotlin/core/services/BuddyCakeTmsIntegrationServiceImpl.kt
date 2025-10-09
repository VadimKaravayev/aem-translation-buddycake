package com.ddy.kotlin.core.services

import com.ddy.kotlin.core.dto.TranslationJob
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class BuddyCakeTmsIntegrationServiceImpl : TmsIntegrationService {

    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    override fun createTranslationJob(job: TranslationJob): String {
        TODO("Not yet implemented")
    }
}