package br.com.agrovisionti.api.controller;

import br.com.agrovisionti.api.dto.auth.LoginRequest;
import br.com.agrovisionti.api.dto.auth.LoginResponse;
import br.com.agrovisionti.api.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
