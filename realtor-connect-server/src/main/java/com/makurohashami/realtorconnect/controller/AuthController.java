package com.makurohashami.realtorconnect.controller;

import com.makurohashami.realtorconnect.dto.apiresponse.ApiSuccess;
import com.makurohashami.realtorconnect.dto.auth.AuthRequest;
import com.makurohashami.realtorconnect.dto.auth.AuthResponse;
import com.makurohashami.realtorconnect.dto.realtor.RealtorAddDto;
import com.makurohashami.realtorconnect.dto.realtor.RealtorFullDto;
import com.makurohashami.realtorconnect.dto.user.UserAddDto;
import com.makurohashami.realtorconnect.dto.user.UserFullDto;
import com.makurohashami.realtorconnect.entity.user.Role;
import com.makurohashami.realtorconnect.service.auth.AuthService;
import com.makurohashami.realtorconnect.service.realtor.RealtorService;
import com.makurohashami.realtorconnect.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.makurohashami.realtorconnect.util.ApiResponseUtil.created;
import static com.makurohashami.realtorconnect.util.ApiResponseUtil.ok;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/auth", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Auth Controller", description = "Authentication endpoints")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final RealtorService realtorService;

    @PreAuthorize("isAnonymous()")
    @PostMapping("/login")
    @Operation(summary = "Login into Realtor Connect")
    public ResponseEntity<ApiSuccess<AuthResponse>> authenticate(@RequestBody AuthRequest request) {
        return ok(authService.authenticate(request));
    }

    @PreAuthorize("isAnonymous()")
    @PostMapping("/register/realtor")
    @Operation(summary = "Register realtor")
    public ResponseEntity<ApiSuccess<RealtorFullDto>> registration(@RequestBody @Valid RealtorAddDto dto) {
        return created(realtorService.create(dto));
    }

    @PreAuthorize("isAnonymous()")
    @PostMapping("/register/user")
    @Operation(summary = "Register user")
    public ResponseEntity<ApiSuccess<UserFullDto>> registration(@RequestBody @Valid UserAddDto dto) {
        return created(userService.create(dto, Role.USER));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/current")
    @Operation(summary = "Get current authenticated user")
    public ResponseEntity<ApiSuccess<UserFullDto>> getCurrent() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ok(userService.readFullByUsername(username));
    }

}
