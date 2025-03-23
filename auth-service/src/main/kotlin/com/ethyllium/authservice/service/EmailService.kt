package com.ethyllium.authservice.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import java.nio.charset.StandardCharsets

@Service
class EmailService(
    private val mailSender: JavaMailSender,
    private val templateEngine: TemplateEngine,
    @Value("\${app.base}") private val appBase: String,
) {

    fun sendVerificationEmail(to: String, name: String, verificationToken: String) {
        val message = mailSender.createMimeMessage()
        val helper = MimeMessageHelper(message, true, StandardCharsets.UTF_8.name())

        helper.setTo(to)
        helper.setSubject("Verify Your Account")

        val context = Context()
        context.setVariable("name", name)
        context.setVariable("verificationUrl", "${appBase}/auth/verify?token=$verificationToken")
        context.setVariable("expirationMinutes", 15)

        val content = templateEngine.process("verification-email", context)
        helper.setText(content, true)

        mailSender.send(message)
    }
}