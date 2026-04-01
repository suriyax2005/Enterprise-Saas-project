package com.saas.saas.controller;

import com.saas.saas.dto.CreateInvitationRequest;
import com.saas.saas.dto.CreateOrganizationRequest;
import com.saas.saas.dto.InvitationResponse;
import com.saas.saas.dto.OrganizationResponse;
import com.saas.saas.service.OrganizationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/api/organization")
public class OrganizationController {

    private final OrganizationService organizationService;

    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @GetMapping("/my")
    public ResponseEntity<OrganizationResponse> getMyOrganization() {
        return ResponseEntity.ok(organizationService.getOrCreateMyOrganization());
    }

    @PutMapping("/my")
    public ResponseEntity<OrganizationResponse> updateOrganization(
            @Valid @RequestBody CreateOrganizationRequest request
    ) {
        return ResponseEntity.ok(organizationService.updateOrganization(request));
    }

    @PostMapping("/invitations")
    public ResponseEntity<InvitationResponse> sendInvitation(
            @Valid @RequestBody CreateInvitationRequest request
    ) {
        return ResponseEntity.ok(organizationService.sendInvitation(request));
    }

    @GetMapping("/invitations")
    public ResponseEntity<List<InvitationResponse>> getInvitations() {
        return ResponseEntity.ok(organizationService.getInvitations());
    }

    @GetMapping("/invitations/detail")
    public ResponseEntity<Map<String, Object>> getInvitationDetail(
            @RequestParam("token") String token
    ) {
        return ResponseEntity.ok(organizationService.getInvitationDetails(token));
    }

    @PostMapping("/invitations/accept")
    public ResponseEntity<Map<String, String>> acceptInvitation(
            @RequestParam("token") String token
    ) {
        String result = organizationService.acceptInvitation(token);
        return ResponseEntity.ok(Map.of("message", result));
    }

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        return ResponseEntity.ok(organizationService.getStatistics());
    }
}
