package com.saas.saas.service;

import com.saas.saas.entity.Notification;
import com.saas.saas.entity.User;
import com.saas.saas.exception.ResourceNotFoundException;
import com.saas.saas.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service managing user notifications.
 * Extensible to SMS and Push triggers via concrete provider integrations.
 */
@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    /**
     * Creates and stores a notification. Stubs out SMS and PUSH dispatches.
     */
    @Transactional
    public Notification createNotification(User user, String message, String type) {
        Notification notification = new Notification(user, message, type);
        notificationRepository.save(notification);

        logger.info("Notification created for user {}: {}", user.getEmail(), message);

        // Extensibility stubs for SMS and Push Notifications
        if ("SMS".equalsIgnoreCase(type)) {
            sendSmsNotificationStub(user.getEmail(), message);
        } else if ("PUSH".equalsIgnoreCase(type)) {
            sendPushNotificationStub(user.getEmail(), message);
        }

        return notification;
    }

    /**
     * Retrieves notifications for a specific user.
     */
    @Transactional(readOnly = true)
    public List<Notification> getNotificationsForUser(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * Retrieves unread notifications count.
     */
    @Transactional(readOnly = true)
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    /**
     * Marks a notification as read.
     */
    @Transactional
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    private void sendSmsNotificationStub(String userEmail, String message) {
        // SMS Gateway integration stub (e.g. Twilio)
        logger.info("SMS GATEWAY STUB: Sending SMS notification to phone linked with user {}: {}", userEmail, message);
    }

    private void sendPushNotificationStub(String userEmail, String message) {
        // Push Gateway integration stub (e.g. Firebase Cloud Messaging - FCM)
        logger.info("PUSH GATEWAY STUB: Sending mobile Push notification to user {}: {}", userEmail, message);
    }
}
