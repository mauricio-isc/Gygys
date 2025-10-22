import { Component, OnInit } from '@angular/core';
import { NotificacionService } from '../../services/notificacion.service';
import { Notificacion } from '../../models/notificacion.model';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatBadgeModule } from '@angular/material/badge';
import { RouterModule } from '@angular/router';
import { CustomAlertService } from '../../services/custom-alert.service';

@Component({
  selector: 'app-notifications',
  templateUrl: './notifications.component.html',
  styleUrls: ['./notifications.component.scss'],
  standalone: true,
  imports: [
    CommonModule,
    MatButtonModule,
    MatIconModule,
    MatBadgeModule,
    RouterModule,
  ]
})
export class NotificationsComponent implements OnInit {
  notifications: Notificacion[] = [];
  loading = false;
  filter: 'all' | 'unread' | 'read' = 'all';
  unreadCount = 0;

  constructor(
    private notificacionService: NotificacionService,
    private customAlertService: CustomAlertService
  ) {}

  ngOnInit(): void {
    this.loadNotifications();
  }

  loadNotifications(): void {
    this.loading = true;
    
    let serviceCall;
    switch (this.filter) {
      case 'unread':
        serviceCall = this.notificacionService.findUnread();
        break;
      case 'read':
        serviceCall = this.notificacionService.findAll();
        break;
      default:
        serviceCall = this.notificacionService.findAll();
    }

    serviceCall.subscribe({
      next: (notifications) => {
        if (this.filter === 'read') {
          this.notifications = notifications.filter(n => n.leida);
        } else {
          this.notifications = notifications;
        }
        this.loading = false;
        
        this.unreadCount = this.notifications.filter(n => !n.leida).length;
      },
      error: (error) => {
        console.error('Error loading notifications:', error);
        this.loading = false;
        this.customAlertService.showError('Error', 'No se pudieron cargar las notificaciones');
      }
    });
  }

  markAsRead(notification: Notificacion): void {
    if (!notification.leida) {
      this.notificacionService.markAsRead(notification.id).subscribe({
        next: (updatedNotification) => {
          notification.leida = true;
          notification.fechaLectura = updatedNotification.fechaLectura;
          this.unreadCount--;
          this.customAlertService.showSuccess('Éxito', 'Notificación marcada como leída');
        },
        error: (error) => {
          console.error('Error marking notification as read:', error);
          this.customAlertService.showError('Error', 'No se pudo marcar como leída');
        }
      });
    }
  }

markAllAsRead(): void {
  const unreadNotifications = this.notifications.filter(n => !n.leida);
  
  if (unreadNotifications.length === 0) {
    this.customAlertService.showInfo('Info', 'No hay notificaciones pendientes');
    return;
  }

  this.customAlertService.showConfirm(
    '¿Marcar todas como leídas?',
    `Se marcarán ${unreadNotifications.length} notificaciones como leídas`,
    'Sí, marcar todas',
    'Cancelar'
  ).subscribe((confirmed) => {
    if (confirmed) {
      const markPromises = unreadNotifications.map(n => 
        this.notificacionService.markAsRead(n.id).toPromise()
      );

      Promise.all(markPromises).then(() => {
        this.customAlertService.showSuccess('Éxito', 'Todas las notificaciones han sido marcadas como leídas');
        this.loadNotifications();
      }).catch((error) => {
        console.error('Error marking all notifications as read:', error);
        this.customAlertService.showError('Error', 'No se pudieron marcar todas las notificaciones');
      });
    }
  });
}

  getNotificationIcon(type: string): string {
    switch (type) {
      case 'VENCIMIENTO_MEMBRESIA':
        return 'fas fa-exclamation-triangle text-warning';
      case 'PAGO_PENDIENTE':
        return 'fas fa-dollar-sign text-info';
      case 'BIENVENIDA':
        return 'fas fa-hand-wave text-success';
      default:
        return 'fas fa-bell text-secondary';
    }
  }

  getRelativeTime(date: Date): string {
    const now = new Date();
    const notificationDate = new Date(date);
    const diffTime = Math.abs(now.getTime() - notificationDate.getTime());
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

    if (diffDays === 0) {
      return 'Hoy';
    } else if (diffDays === 1) {
      return 'Ayer';
    } else if (diffDays < 7) {
      return `Hace ${diffDays} días`;
    } else if (diffDays < 30) {
      const weeks = Math.floor(diffDays / 7);
      return `Hace ${weeks} semana${weeks > 1 ? 's' : ''}`;
    } else {
      const months = Math.floor(diffDays / 30);
      return `Hace ${months} mes${months > 1 ? 'es' : ''}`;
    }
  }

  onFilterChange(): void {
    this.loadNotifications();
  }

  testAutomaticNotifications(): void {
    this.notificacionService.sendPendingNotifications().subscribe({
      next: () => {
        this.customAlertService.showSuccess('Éxito', 'Notificaciones pendientes enviadas');
        this.loadNotifications();
      },
      error: (error) => {
        console.error('Error sending pending notifications:', error);
        this.customAlertService.showError('Error', 'No se pudieron enviar las notificaciones');
      }
    });
  }
}