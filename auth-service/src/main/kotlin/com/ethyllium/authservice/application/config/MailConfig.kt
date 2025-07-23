package com.ethyllium.authservice.application.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl

@Configuration
class MailConfig(
    @Value("\${spring.mail.host}") private val mailHost: String,
    @Value("\${spring.mail.port}") private val mailPort: Int,
    @Value("\${spring.mail.properties.mail.smtp.auth}") private val smtpAuth: Boolean,
    @Value("\${spring.mail.properties.mail.smtp.starttls.enable}") private val startTTLsEnabled: Boolean,
) {
    @Bean
    fun mailSender(): JavaMailSender {
        return JavaMailSenderImpl().apply {
            host = mailHost
            port = mailPort

            val props = javaMailProperties
            props["mail.smtp.auth"] = smtpAuth
            props["mail.smtp.starttls.enable"] = startTTLsEnabled
        }
    }
}