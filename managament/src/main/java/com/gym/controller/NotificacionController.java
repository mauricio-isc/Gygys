package com.gym.controller;

import com.gym.dto.NotificacionResponse;
import com.gym.service.NotificacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class NotificacionController {
    private final NotificacionService notificacionService;

    @GetMapping
    public ResponseEntity<List<NotificacionResponse>> findAll() {
        List<NotificacionResponse> notifications = notificacionService.findAll();
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/unread")
    public ResponseEntity<List<NotificacionResponse>> findUnread() {
        List<NotificacionResponse> notifications = notificacionService.findUnread();
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<NotificacionResponse>> findByMember(@PathVariable Long memberId) {
        List<NotificacionResponse> notifications = notificacionService.findByMiembro(memberId);
        return ResponseEntity.ok(notifications);
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<NotificacionResponse> markAsRead(@PathVariable Long id) {
        NotificacionResponse notification = notificacionService.markAsRead(id);
        return ResponseEntity.ok(notification);
    }

    @PostMapping("/send-pending")
    public ResponseEntity<Void> sendPendingNotifications() {
        notificacionService.enviarNotificacionesPendientes();
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/stats", produces = "application/json")
    public ResponseEntity<NotificationStats> getNotificationStats() {
        NotificationStats stats = new NotificationStats();
        stats.setUnreadCount(notificacionService.countUnread());
        stats.setReadCount(notificacionService.countRead());
        stats.setPendingCount(notificacionService.countPending());
        return ResponseEntity.ok(stats);
    }

    public static class NotificationStats {
        private long unreadCount;
        private long readCount;
        private long pendingCount;

        // Getters and setters
        public long getUnreadCount() {
            return unreadCount;
        }

        public void setUnreadCount(long unreadCount) {
            this.unreadCount = unreadCount;
        }

        public long getReadCount() {
            return readCount;
        }

        public void setReadCount(long readCount) {
            this.readCount = readCount;
        }

        public long getPendingCount() {
            return pendingCount;
        }

        public void setPendingCount(long pendingCount) {
            this.pendingCount = pendingCount;
        }
    }
}
