package com.example.localbusiness.service;

import com.example.localbusiness.model.Reminder;
import com.example.localbusiness.model.User;
import com.example.localbusiness.repository.ReminderRepository;
import com.example.localbusiness.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReminderServiceTest {

    @Mock
    private ReminderRepository reminderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private ReminderService reminderService;

    private User testUser;
    private Reminder testReminder;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .name("Test User")
                .email("test@example.com")
                .password("password")
                .build();

        testReminder = Reminder.builder()
                .id(1L)
                .user(testUser)
                .title("Test Reminder")
                .description("Test Description")
                .reminderTime(LocalDateTime.now().plusHours(1))
                .type(Reminder.ReminderType.GENERAL)
                .sent(false)
                .active(true)
                .build();
    }

    @Test
    void createReminder_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(reminderRepository.save(any(Reminder.class))).thenReturn(testReminder);

        // Act
        Reminder result = reminderService.createReminder(1L, "Test Reminder", "Test Description", 
                LocalDateTime.now().plusHours(1), Reminder.ReminderType.GENERAL);

        // Assert
        assertNotNull(result);
        assertEquals("Test Reminder", result.getTitle());
        assertEquals("Test Description", result.getDescription());
        verify(reminderRepository).save(any(Reminder.class));
    }

    @Test
    void createReminder_UserNotFound() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
                reminderService.createReminder(1L, "Test Reminder", "Test Description", 
                        LocalDateTime.now().plusHours(1), Reminder.ReminderType.GENERAL));
    }

    @Test
    void getUserReminders_Success() {
        // Arrange
        List<Reminder> expectedReminders = Arrays.asList(testReminder);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(reminderRepository.findByUserAndActive(testUser, true)).thenReturn(expectedReminders);

        // Act
        List<Reminder> result = reminderService.getUserReminders(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Reminder", result.get(0).getTitle());
    }

    @Test
    void updateReminder_Success() {
        // Arrange
        when(reminderRepository.findById(1L)).thenReturn(Optional.of(testReminder));
        when(reminderRepository.save(any(Reminder.class))).thenReturn(testReminder);

        // Act
        reminderService.updateReminder(1L, "Updated Title", "Updated Description", 
                LocalDateTime.now().plusHours(2));

        // Assert
        verify(reminderRepository).save(any(Reminder.class));
    }

    @Test
    void updateReminder_ReminderNotFound() {
        // Arrange
        when(reminderRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
                reminderService.updateReminder(1L, "Updated Title", "Updated Description", 
                        LocalDateTime.now().plusHours(2)));
    }

    @Test
    void deleteReminder_Success() {
        // Arrange
        when(reminderRepository.findById(1L)).thenReturn(Optional.of(testReminder));
        when(reminderRepository.save(any(Reminder.class))).thenReturn(testReminder);

        // Act
        reminderService.deleteReminder(1L);

        // Assert
        verify(reminderRepository).save(any(Reminder.class));
        assertFalse(testReminder.isActive());
    }

    @Test
    void createOrderReminder_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(reminderRepository.save(any(Reminder.class))).thenReturn(testReminder);

        // Act
        reminderService.createOrderReminder(1L, "ORDER123", LocalDateTime.now().plusHours(1));

        // Assert
        verify(reminderRepository).save(any(Reminder.class));
    }

    @Test
    void createPaymentReminder_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(reminderRepository.save(any(Reminder.class))).thenReturn(testReminder);

        // Act
        reminderService.createPaymentReminder(1L, "ORDER123", LocalDateTime.now().plusHours(1));

        // Assert
        verify(reminderRepository).save(any(Reminder.class));
    }
} 