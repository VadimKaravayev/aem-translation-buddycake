package com.ddy.kotlin.core.services.impl

import com.ddy.kotlin.core.services.GreetService
import org.osgi.service.component.annotations.Component

@Component(service = [GreetService::class])
class GreetServiceImpl : GreetService {
    override fun greet(name: String): String {
        return "Kotlin solutes you, dear $name"
    }
}