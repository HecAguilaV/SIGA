package com.siga.agent.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(GeminiProperties::class)
class AgentConfig

@ConfigurationProperties(prefix = "gemini")
data class GeminiProperties(
    var apiKey: String = "",
    var modelId: String = "gemini-2.0-flash-001"
)
