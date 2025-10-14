import { Component, OnInit } from '@angular/core';
import { NotificacionService } from '../../services/notificacion.service';
import { Notificacion } from '../../models/notificacion.model';
import Swal from 'sweetalert2';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatBadgeModule } from '@angular/material/badge';
import { RouterModule } from '@angular/router';

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

  constructor(private notificacionService: NotificacionService) {}

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
        
        // Calcular contador de no leídas
        this.unreadCount = this.notifications.filter(n => !n.leida).length;
      },
      error: (error) => {
        console.error('Error loading notifications:', error);
        this.loading = false;
        Swal.fire('Error', 'No se pudieron cargar las notificaciones', 'error');
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
          Swal.fire('Éxito', 'Notificación marcada como leída', 'success');
        },
        error: (error) => {
          console.error('Error marking notification as read:', error);
          Swal.fire('Error', 'No se pudo marcar como leída', 'error');
        }
      });
    }
  }

  markAllAsRead(): void {
    const unreadNotifications = this.notifications.filter(n => !n.leida);
    
    if (unreadNotifications.length === 0) {
      Swal.fire('Info', 'No hay notificaciones pendientes', 'info');
      return;
    }

    Swal.fire({
      title: '¿Marcar todas como leídas?',
      text: `Se marcarán ${unreadNotifications.length} notificaciones como leídas`,
      icon: 'question',
      showCancelButton: true,
      confirmButtonText: 'Sí, marcar todas',
      cancelButtonText: 'Cancelar'
    }).then((result) => {
      if (result.isConfirmed) {
        const markPromises = unreadNotifications.map(n => 
          this.notificacionService.markAsRead(n.id).toPromise()
        );

        Promise.all(markPromises).then(() => {
          Swal.fire('Éxito', 'Todas las notificaciones han sido marcadas como leídas', 'success');
          this.loadNotifications();
        }).catch((error) => {
          console.error('Error marking all notifications as read:', error);
          Swal.fire('Error', 'No se pudieron marcar todas las notificaciones', 'error');
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

  // Método para probar notificaciones automáticas
  testAutomaticNotifications(): void {
    this.notificacionService.sendPendingNotifications().subscribe({
      next: () => {
        Swal.fire('Éxito', 'Notificaciones pendientes enviadas', 'success');
        this.loadNotifications();
      },
      error: (error) => {
        console.error('Error sending pending notifications:', error);
        Swal.fire('Error', 'No se pudieron enviar las notificaciones', 'error');
      }
    });
  }
}