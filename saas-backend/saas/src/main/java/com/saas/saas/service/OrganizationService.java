package com.saas.saas.service;

import com.saas.saas.builder.OrganizationBuilder;
import com.saas.saas.dto.CreateInvitationRequest;
import com.saas.saas.dto.CreateOrganizationRequest;
import com.saas.saas.dto.InvitationResponse;
import com.saas.saas.dto.OrganizationResponse;
import com.saas.saas.entity.Organization;
import com.saas.saas.entity.OrganizationInvitation;
import com.saas.saas.entity.Tenant;
import com.saas.saas.entity.User;
import com.saas.saas.exception.BadRequestException;
import com.saas.saas.exception.ResourceNotFoundException;
import com.saas.saas.exception.UnauthorizedException;
import com.saas.saas.repository.OrganizationInvitationRepository;
import com.saas.saas.repository.OrganizationRepository;
import com.saas.saas.repository.TenantRepository;
import com.saas.saas.repository.UserRepository;
import com.saas.saas.security.CurrentUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final TenantRepository tenantRepository;
    private final OrganizationInvitationRepository invitationRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public OrganizationService(
            OrganizationRepository organizationRepository,
            TenantRepository tenantRepository,
            OrganizationInvitationRepository invitationRepository,
            UserRepository userRepository,
            EmailService emailService
    ) {
        this.organizationRepository = organizationRepository;
        this.tenantRepository = tenantRepository;
        this.invitationRepository = invitationRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    private CurrentUser getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof CurrentUser)) {
            throw new UnauthorizedException("User is not authenticated");
        }
        return (CurrentUser) auth.getPrincipal();
    }

    @Transactional
    public OrganizationResponse getOrCreateMyOrganization() {
        CurrentUser currentUser = getCurrentUser();
        Long tenantId = currentUser.getTenantId();

        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found for ID: " + tenantId));

        Organization organization = organizationRepository.findByTenantId(tenantId)
                .orElseGet(() -> {
                    Organization newOrg = new Organization();
                    newOrg.setTenant(tenant);
                    newOrg.setDescription("Welcome to " + tenant.getName() + " Multi-Tenant SaaS workspace.");
                    newOrg.setLogoUrl("");
                    return organizationRepository.save(newOrg);
                });

        return OrganizationBuilder.toResponse(organization);
    }

    @Transactional
    public OrganizationResponse updateOrganization(CreateOrganizationRequest request) {
        CurrentUser currentUser = getCurrentUser();
        Long tenantId = currentUser.getTenantId();

        Organization organization = organizationRepository.findByTenantId(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization details not found for Tenant: " + tenantId));

        Tenant tenant = organization.getTenant();
        tenant.setName(request.getName());
        tenantRepository.save(tenant);

        organization.setDescription(request.getDescription());
        organization.setLogoUrl(request.getLogoUrl());
        Organization updated = organizationRepository.save(organization);

        return OrganizationBuilder.toResponse(updated);
    }

    @Transactional
    public InvitationResponse sendInvitation(CreateInvitationRequest request) {
        CurrentUser currentUser = getCurrentUser();
        Long tenantId = currentUser.getTenantId();

        Organization organization = organizationRepository.findByTenantId(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found for Tenant: " + tenantId));

        // Prevent inviting already existing team member
        Optional<User> existingUser = userRepository.findByEmail(request.getEmail().trim().toLowerCase());
        if (existingUser.isPresent() && existingUser.get().getTenant().getId().equals(tenantId)) {
            throw new BadRequestException("User with this email is already a member of your Organization.");
        }

        String token = UUID.randomUUID().toString();
        OrganizationInvitation invitation = new OrganizationInvitation();
        invitation.setOrganization(organization);
        invitation.setEmail(request.getEmail().trim().toLowerCase());
        invitation.setRole(request.getRole().trim().toUpperCase());
        invitation.setInviteToken(token);
        invitation.setExpiresAt(LocalDateTime.now().plusDays(7)); // Valid for 7 days

        OrganizationInvitation saved = invitationRepository.save(invitation);

        String invitationLink = "http://localhost:5173/register?inviteToken=" + token;
        String emailBody = String.format(
                "You have been invited to join the Organization '%s' as an %s.\n\n"
                        + "Please click the link below to register and accept the invitation:\n\n%s",
                organization.getTenant().getName(),
                request.getRole(),
                invitationLink
        );

        emailService.sendEmail(request.getEmail(), "Invitation to join " + organization.getTenant().getName(), emailBody);

        return OrganizationBuilder.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<InvitationResponse> getInvitations() {
        CurrentUser currentUser = getCurrentUser();
        Long tenantId = currentUser.getTenantId();

        List<OrganizationInvitation> invitations = invitationRepository.findByOrganizationTenantId(tenantId);
        List<InvitationResponse> responses = new ArrayList<>();
        for (OrganizationInvitation inv : invitations) {
            responses.add(OrganizationBuilder.toResponse(inv));
        }
        return responses;
    }

    @Transactional
    public String acceptInvitation(String token) {
        OrganizationInvitation invitation = invitationRepository.findByInviteToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid invitation token."));

        if (invitation.isAccepted()) {
            throw new BadRequestException("Invitation has already been accepted.");
        }

        if (LocalDateTime.now().isAfter(invitation.getExpiresAt())) {
            throw new BadRequestException("Invitation token has expired.");
        }

        CurrentUser currentUser = getCurrentUser();
        User user = userRepository.findByEmail(currentUser.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Logged-in user not found."));

        // Bind user to the organization's tenant and role
        user.setTenant(invitation.getOrganization().getTenant());
        user.setRole(invitation.getRole());
        userRepository.save(user);

        invitation.setAccepted(true);
        invitationRepository.save(invitation);

        return "Invitation accepted. You are now a member of " + invitation.getOrganization().getTenant().getName();
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getInvitationDetails(String token) {
        OrganizationInvitation invitation = invitationRepository.findByInviteToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid invitation token."));

        if (invitation.isAccepted()) {
            throw new BadRequestException("Invitation has already been accepted.");
        }

        if (LocalDateTime.now().isAfter(invitation.getExpiresAt())) {
            throw new BadRequestException("Invitation token has expired.");
        }

        Map<String, Object> details = new HashMap<>();
        details.put("tenantId", invitation.getOrganization().getTenant().getId());
        details.put("email", invitation.getEmail());
        details.put("role", invitation.getRole());
        details.put("organizationName", invitation.getOrganization().getTenant().getName());

        return details;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getStatistics() {
        CurrentUser currentUser = getCurrentUser();
        Long tenantId = currentUser.getTenantId();

        // Calculate simple statistics (Total Members, Active invitations, Hired members, etc.)
        List<User> members = userRepository.findByTenantId(tenantId);
        long activeInvitations = invitationRepository.findByOrganizationTenantId(tenantId)
                .stream()
                .filter(inv -> !inv.isAccepted() && LocalDateTime.now().isBefore(inv.getExpiresAt()))
                .count();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalMembers", members.size());
        stats.put("activeInvitations", activeInvitations);
        stats.put("organizationName", tenantRepository.findById(tenantId).map(Tenant::getName).orElse("Unknown"));

        return stats;
    }
}
