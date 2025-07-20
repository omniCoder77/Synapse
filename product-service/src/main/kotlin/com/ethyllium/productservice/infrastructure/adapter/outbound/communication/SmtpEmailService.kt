package com.ethyllium.productservice.infrastructure.adapter.outbound.communication

import com.ethyllium.productservice.domain.port.driven.EmailService
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Component
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import java.nio.charset.StandardCharsets

@Component
class SmtpEmailService(
    private val mailSender: JavaMailSender,
    private val templateEngine: TemplateEngine,
    @Value("\${app.base}") private val appBase: String,
) : EmailService {
    override fun sendVerificationEmail(to: String, token: String, expirationMinutes: Int) {
        val message = mailSender.createMimeMessage()
        val helper = MimeMessageHelper(message, true, StandardCharsets.UTF_8.name())
        val context = Context()

        helper.setTo(to)
        helper.setSubject("Verify Your Account")

        context.setVariable("verificationUrl", "${appBase}/auth/verify?token=$token")
        context.setVariable("expirationMinutes", expirationMinutes)

        val content = templateEngine.process("verification-email", context)
        helper.setText(content, true)

        mailSender.send(message)
    }
}