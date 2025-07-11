package com.example.bankcards.controller.admin;

import com.example.bankcards.dto.user.ParamSearchAdminUser;
import com.example.bankcards.dto.user.RegistrationRequestDto;
import com.example.bankcards.dto.user.UserResponse;
import com.example.bankcards.service.AuthenticationService;
import com.example.bankcards.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/users")
@Slf4j
@Validated
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminUsersController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createUser(@Valid
                                   @RequestBody RegistrationRequestDto registrationDto) {
        log.info("==> create user = {}", registrationDto);
        return authenticationService.register(registrationDto);
    }

    @GetMapping
    public Page<UserResponse> findAllUsers(@RequestParam(defaultValue = "0", required = false) @PositiveOrZero Integer offset,
                                           @RequestParam(defaultValue = "10", required = false) @PositiveOrZero @Max(100) Integer limit) {
        log.info("==> findAllUsers from={}, size={}", offset, limit);
        ParamSearchAdminUser paramSearch = new ParamSearchAdminUser(offset, limit);
        return userService.findAllUsers(paramSearch);
    }

    @GetMapping("/{userId}")
    public UserResponse getUser(@PathVariable Long userId) {
        log.info("==> get user, id = {}", userId);
        return userService.getUserById(userId);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@NotNull
                           @PathVariable long userId) {
        log.info("==> deleteUser requestBody={}", userId);
        userService.deleteUser(userId);
    }
}
