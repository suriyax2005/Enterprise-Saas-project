package com.saas.saas.builder;

import com.saas.saas.dto.InvitationResponse;
import com.saas.saas.dto.OrganizationResponse;
import com.saas.saas.entity.Organization;
import com.saas.saas.entity.OrganizationInvitation;

public final class OrganizationBuilder {

    private OrganizationBuilder() {
    }

    public static OrganizationResponse toResponse(Organization organization) {
        if (organization == null) {
            return null;
        }
        OrganizationResponse response = new OrganizationResponse();
        response.setId(organization.getId());
        response.setTenantId(organization.getTenant().getId());
        response.setName(organization.getTenant().getName());
        response.setDescription(organization.getDescription());
        response.setLogoUrl(organization.getLogoUrl());
        response.setCreatedAt(organization.getCreatedAt());
        return response;
    }

    public static InvitationResponse toResponse(OrganizationInvitation invitation) {
        if (invitation == null) {
            return null;
        }
        InvitationResponse response = new InvitationResponse();
        response.setId(invitation.getId());
        response.setOrganizationId(invitation.getOrganization().getId());
        response.setOrganizationName(invitation.getOrganization().getTenant().getName());
        response.setEmail(invitation.getEmail());
        response.setRole(invitation.getRole());
        response.setInviteToken(invitation.getInviteToken());
        response.setAccepted(invitation.isAccepted());
        response.setExpiresAt(invitation.getExpiresAt());
        response.setCreatedAt(invitation.getCreatedAt());
        return response;
    }
}
