package com.saas.saas.repository;

import com.saas.saas.entity.OrganizationInvitation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface OrganizationInvitationRepository extends JpaRepository<OrganizationInvitation, Long> {

    Optional<OrganizationInvitation> findByInviteToken(String inviteToken);

    List<OrganizationInvitation> findByOrganizationTenantId(Long tenantId);
}
