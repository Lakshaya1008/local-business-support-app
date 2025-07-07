package com.example.localbusiness.service;

import com.example.localbusiness.model.Reminder;
import com.example.localbusiness.model.User;
import com.example.localbusiness.repository.ReminderRepository;
import com.example.localbusiness.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReminderService {
    
    private final ReminderRepository reminderRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    
    @Transactional
    public Reminder createReminder(Long userId, String title, String description, LocalDateTime reminderTime, Reminder.ReminderType type) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
            
        Reminder reminder = Reminder.builder()
            .user(user)
            .title(title)
            .description(description)
            .reminderTime(reminderTime)
            .type(type)
            .build();
            
        return reminderRepository.save(reminder);
    }
    
    public List<Reminder> getUserReminders(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return reminderRepository.findByUserAndActive(user, true);
    }
    
    @Transactional
    public void updateReminder(Long reminderId, String title, String description, LocalDateTime reminderTime) {
        Reminder reminder = reminderRepository.findById(reminderId)
            .orElseThrow(() -> new RuntimeException("Reminder not found"));
            
        reminder.setTitle(title);
        reminder.setDescription(description);
        reminder.setReminderTime(reminderTime);
        
        reminderRepository.save(reminder);
    }
    
    @Transactional
    public void deleteReminder(Long reminderId) {
        Reminder reminder = reminderRepository.findById(reminderId)
            .orElseThrow(() -> new RuntimeException("Reminder not found"));
            
        reminder.setActive(false);
        reminderRepository.save(reminder);
    }
    
    @Scheduled(fixedRate = 60000) // Run every minute
    @Transactional
    public void sendScheduledReminders() {
        try {
            LocalDateTime now = LocalDateTime.now();
            List<Reminder> pendingReminders = reminderRepository.findPendingReminders(now);
            
            for (Reminder reminder : pendingReminders) {
                sendReminderEmail(reminder);
                reminder.setSent(true);
                reminderRepository.save(reminder);
            }
            
            if (!pendingReminders.isEmpty()) {
                log.info("Sent {} reminder emails", pendingReminders.size());
            }
        } catch (Exception e) {
            log.error("Error sending scheduled reminders", e);
        }
    }
    
    private void sendReminderEmail(Reminder reminder) {
        try {
            String subject = "Reminder: " + reminder.getTitle();
            String content = String.format(
                "Hello %s,\n\n" +
                "This is a reminder for: %s\n\n" +
                "Details: %s\n\n" +
                "Best regards,\nLocal Business Support Team",
                reminder.getUser().getName(),
                reminder.getTitle(),
                reminder.getDescription()
            );
            
            emailService.sendEmail(reminder.getUser().getEmail(), subject, content);
            log.info("Sent reminder email to: {}", reminder.getUser().getEmail());
        } catch (Exception e) {
            log.error("Error sending reminder email for reminder ID: {}", reminder.getId(), e);
        }
    }
    
    public void createOrderReminder(Long userId, String orderId, LocalDateTime reminderTime) {
        createReminder(
            userId,
            "Order Update Reminder",
            "Your order #" + orderId + " has been updated. Please check your order status.",
            reminderTime,
            Reminder.ReminderType.ORDER_UPDATE
        );
    }
    
    public void createPaymentReminder(Long userId, String orderId, LocalDateTime reminderTime) {
        createReminder(
            userId,
            "Payment Due Reminder",
            "Payment for order #" + orderId + " is due. Please complete your payment.",
            reminderTime,
            Reminder.ReminderType.PAYMENT_DUE
        );
    }
} 