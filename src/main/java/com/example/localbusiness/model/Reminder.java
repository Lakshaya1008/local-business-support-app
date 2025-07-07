package com.example.localbusiness.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "reminders")
public class Reminder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private LocalDateTime reminderTime;

    @Column(nullable = false)
    @Builder.Default
    private boolean sent = false;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ReminderType type = ReminderType.GENERAL;

    public enum ReminderType {
        GENERAL,
        ORDER_UPDATE,
        PAYMENT_DUE,
        SHIPPING_UPDATE
    }
} 