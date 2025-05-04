package com.ethyllium.authservice.infrastructure.web.rest

import com.ethyllium.authservice.application.service.ValidationService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class VerificationController(private val validationService: ValidationService) {

    @GetMapping("/verify")
    fun verify(@RequestParam(value = "token", required = true) token: String): ResponseEntity<String> {
        if (validationService.verifyAccount(token)) {
            return ResponseEntity.ok("OK")
        } else return ResponseEntity.notFound().build()
    }
}