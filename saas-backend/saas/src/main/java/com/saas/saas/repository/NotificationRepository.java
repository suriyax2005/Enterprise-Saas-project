package com.saas.saas.repository;

import com.saas.saas.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Standard Spring Data JPA repository for notification entries.
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Finds notifications for a specific user, sorted from newest to oldest.
     */
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * Counts the number of unread notifications for a user.
     */
    long countByUserIdAndIsReadFalse(Long userId);
}
