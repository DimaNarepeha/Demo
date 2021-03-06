/*
 *Open source 2019
 */
package com.softserve.demo.service.impl;

import com.softserve.demo.exceptions.NotFoundException;
import com.softserve.demo.model.Notification;
import com.softserve.demo.model.User;
import com.softserve.demo.repository.NotificationRepository;
import com.softserve.demo.repository.UserRepository;
import com.softserve.demo.service.EmailService;
import com.softserve.demo.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of Notification service
 * to send, set as seen notifications via UI.
 *
 * @author Dmytro Narepekha
 */
@Service
@Transactional
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private static final String USER_NOTIFIED = "User with id [%s] was notified!";
    private static final String USER_NOT_FOUND = "User with id [%s] was not found!";
    private static final String NOTIFICATION_NOT_FOUND = "Notification with id [%s] was not found";
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final EmailService emailService;

    public NotificationServiceImpl(final UserRepository userRepository,
                                   final NotificationRepository notificationRepository,
                                   final EmailService emailService) {
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
        this.emailService = emailService;
    }

    private void sendNotificationToEmail(final User user, final Notification notification) {
        emailService.sendEmailTo(user.getEmail(), notification.getHeader(), notification.getMessage());
    }

    private void validateUserNotificationList(User user) {
        if (user.getNotifications() == null) {
            user.setNotifications(new ArrayList<>());
        }
    }

    /**
     * Simply notifies user by id.
     * You don't need to set time.
     * It will be set inside the method.
     *
     * @param userId       id of user to notify
     * @param notification notification for user
     */
    @Override
    public void notifyByUserId(final Integer userId, final Notification notification) {
        User userToNotify = userRepository.findById(userId);
        if (userToNotify == null) {
            throw new NotFoundException(String.format(USER_NOT_FOUND, userId));
        }
        validateUserNotificationList(userToNotify);
        notification.setTime(LocalDateTime.now());
        notification.setSeen(false);
        Notification persistedNotification = notificationRepository.save(notification);
        userToNotify.getNotifications().add(persistedNotification);
        sendNotificationToEmail(userToNotify, notification);
        log.debug(String.format(USER_NOTIFIED, userId));
    }

    @Override
    public List<Notification> getNotificationsByUserId(final Integer userId) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new NotFoundException(String.format(USER_NOT_FOUND, userId));
        }
        return user.getNotifications();
    }

    /**
     * This method set the notification status as seen.
     *
     * @param notificationId id of notification to be set as seen.
     */
    @Override
    public void setNotificationSeen(final Integer notificationId) {
        Notification notification = notificationRepository
                .findById(notificationId)
                .orElseThrow(() -> new NotFoundException(String.format(NOTIFICATION_NOT_FOUND, notificationId)));
        notification.setSeen(true);
    }
}
