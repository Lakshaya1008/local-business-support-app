package com.example.localbusiness.repository;

import com.example.localbusiness.model.Reminder;
import com.example.localbusiness.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Long> {
    
    List<Reminder> findByUser(User user);
    
    List<Reminder> findByUserAndActive(User user, boolean active);
    
    @Query("SELECT r FROM Reminder r WHERE r.reminderTime <= :now AND r.sent = false AND r.active = true")
    List<Reminder> findPendingReminders(@Param("now") LocalDateTime now);
    
    @Query("SELECT r FROM Reminder r WHERE r.user = :user AND r.reminderTime BETWEEN :start AND :end AND r.active = true")
    List<Reminder> findRemindersByUserAndTimeRange(
        @Param("user") User user, 
        @Param("start") LocalDateTime start, 
        @Param("end") LocalDateTime end
    );
} 