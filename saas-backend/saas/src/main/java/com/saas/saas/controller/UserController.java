package com.saas.saas.controller;

import com.saas.saas.dto.UserResponse;
import com.saas.saas.entity.User;
import com.saas.saas.repository.UserRepository;
import com.saas.saas.repository.UserSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller exposing directory management and search options for admin panels.
 * Path is restricted to users with ADMIN authority via SecurityConfig matches.
 */
@RestController
@RequestMapping("/v1/api/admin/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Searches, filters, and paginates users database records.
     */
    @GetMapping("/search")
    public ResponseEntity<Page<UserResponse>> searchUsers(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "role", required = false) String role,
            @RequestParam(value = "tenantId", required = false) Long tenantId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir
    ) {
        // 1. Build pagination & sorting directions
        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() :
                Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        // 2. Build dynamic queries using JPA Specification executor
        Specification<User> spec = UserSpecification.filterUsers(name, email, role, tenantId);

        // 3. Query repository and map DB entities to DTO responses
        Page<User> usersPage = userRepository.findAll(spec, pageable);
        Page<UserResponse> mappedPage = usersPage.map(user -> new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getTenant() != null ? user.getTenant().getId() : null,
                user.isEmailVerified()
        ));

        return ResponseEntity.ok(mappedPage);
    }
}