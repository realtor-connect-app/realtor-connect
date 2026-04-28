package com.makurohashami.realtorconnect.controller;

import com.makurohashami.realtorconnect.dto.apiresponse.ApiSuccess;
import com.makurohashami.realtorconnect.dto.user.UserAddDto;
import com.makurohashami.realtorconnect.dto.user.UserFilter;
import com.makurohashami.realtorconnect.dto.user.UserFullDto;
import com.makurohashami.realtorconnect.entity.user.Role;
import com.makurohashami.realtorconnect.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.makurohashami.realtorconnect.util.ApiResponseUtil.created;
import static com.makurohashami.realtorconnect.util.ApiResponseUtil.ok;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/admins", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Admin Management Controller", description = "Allows you manage admins")
public class AdminManagementController {

    private final UserService userService;

    @PostMapping
    @Operation(summary = "Create admin")
    @PreAuthorize("hasAuthority('MANAGE_ADMINS')")
    public ResponseEntity<ApiSuccess<UserFullDto>> createAdmin(@RequestBody @Valid UserAddDto dto) {
        return created(userService.create(dto, Role.ADMIN));
    }

    @GetMapping
    @Operation(summary = "Get all admins")
    @PreAuthorize("hasAuthority('MANAGE_ADMINS')")
    public ResponseEntity<ApiSuccess<List<UserFullDto>>> getAllAdmins() {
        return ok(userService.readAllFulls(UserFilter.builder().roles(List.of(Role.ADMIN)).build()));
    }

}
