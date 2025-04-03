package com.ethyllium.authservice.api

import com.ethyllium.authservice.dto.request.RegisterRequest
import com.ethyllium.authservice.dto.response.ApiResponse
import com.ethyllium.authservice.exception.EmailExistsException
import com.ethyllium.authservice.mapper.UserMapper
import com.ethyllium.authservice.service.RegisterService
import org.postgresql.util.PSQLException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class RegisterController(
    private val registerService: RegisterService, private val userMapper: UserMapper
) {

    @PostMapping("/register")
    fun register(@RequestBody registerRequest: RegisterRequest): ResponseEntity<ApiResponse> {
        try {
            val token = registerService.register(userMapper.toUser(registerRequest), registerRequest.deviceFingerprint)
            return ResponseEntity.status(HttpStatus.OK).headers { it.add(HttpHeaders.AUTHORIZATION, token) }
                .body(ApiResponse.Success(token))
        } catch (e: PSQLException) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.Error(e.message!!))
        } catch (e: IllegalArgumentException) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.Error(e.message!!))
        } catch (e: EmailExistsException) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.Error(e.message!!))
        }
    }
}