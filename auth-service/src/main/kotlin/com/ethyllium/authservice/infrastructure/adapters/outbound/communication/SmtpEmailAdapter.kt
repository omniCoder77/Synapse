package com.ethyllium.authservice.infrastructure.adapters.outbound.communication

import com.ethyllium.authservice.domain.port.driven.EmailService
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Component
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import reactor.core.publisher.Mono
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Component
class SmtpEmailAdapter(
    private val mailSender: JavaMailSender,
    private val templateEngine: TemplateEngine,
    @Value("\${app.base}") private val appBase: String,
) : EmailService {

    override fun sendVerificationEmail(to: String, token: String, expirationMinutes: Int): Mono<Void> {
        val message = mailSender.createMimeMessage()
        val helper = MimeMessageHelper(message, true, StandardCharsets.UTF_8.name())
        val context = Context()

        try {
            helper.setTo(to)
            helper.setSubject("Verify Your Account")

            context.setVariable("verificationUrl", "${appBase}/auth/verify?token=$token")
            context.setVariable("expirationMinutes", expirationMinutes)

            val content = templateEngine.process("verification-email", context)
            helper.setText(content, true)

            mailSender.send(message)
            return Mono.empty()
        } catch (e: Exception) {
            return Mono.error(EmailException("Failed to send verification email to $to", e))
        }
    }

    override fun sendLoginEmail(to: String, sessionId: String, expirationMinutes: Int): Mono<Void> {
        val message = mailSender.createMimeMessage()
        val helper = MimeMessageHelper(message, true, StandardCharsets.UTF_8.name())
        val context = Context()

        try {
            helper.setTo(to)
            helper.setSubject("Verify First time logging in with this device")

            context.setVariable("verificationUrl", "${appBase}/auth/verify-email?session=$sessionId")
            context.setVariable("expirationMinutes", expirationMinutes)

            val content = templateEngine.process(
                "verification-email",
                context
            ) // Assuming "verification-email" template is also suitable for login emails
            helper.setText(content, true)

            mailSender.send(message)
        } catch (e: Exception) {
            // Consider logging the error here
            return Mono.error(EmailException("Failed to send login email to $to", e))
        }
        return Mono.empty()
    }

    override fun sendPasswordResetEmail(
        email: String, resetToken: String, expirationMinutes: Int
    ): Mono<Void> {
        val message = mailSender.createMimeMessage()
        val helper = MimeMessageHelper(message, true, StandardCharsets.UTF_8.name())
        val context = Context()

        try {
            helper.setTo(email)
            helper.setSubject("Password Reset Request")

            val resetUrl =
                "$appBase/auth/reset-password?token=${URLEncoder.encode(resetToken, StandardCharsets.UTF_8.name())}"

            context.setVariable("resetUrl", resetUrl)
            context.setVariable("expirationMinutes", expirationMinutes)
            context.setVariable("supportEmail", "support@yourdomain.com")

            val content = templateEngine.process("password-reset-email", context)
            helper.setText(content, true)

            mailSender.send(message)
            return Mono.empty()
        } catch (e: Exception) {
            return Mono.error(EmailException("Failed to send password reset email to $email", e))
        }
    }
}

class EmailException(message: String, cause: Throwable?) : RuntimeException(message, cause)