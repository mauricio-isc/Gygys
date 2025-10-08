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
        // Asumiendo que hay un método para encontrar leídas
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
      },
      error: (error) => {
        console.error('Error loading notifications:', error);
        this.loading = false;
      }
    });
  }

  markAsRead(notification: Notificacion): void {
    if (!notification.leida) {
      this.notificacionService.markAsRead(notification.id).subscribe({
        next: () => {
          notification.leida = true;
          notification.fechaLectura = new Date();
        },
        error: (error) => {
          console.error('Error marking notification as read:', error);
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
      confirmButtonText: 'Sí, marcar todas'
    }).then((result) => {
      if (result.isConfirmed) {
        // Marcar todas las notificaciones como leídas
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

    if (diffDays === 1) {
      return 'Hoy';
    } else if (diffDays === 2) {
      return 'Ayer';
    } else {
      return `Hace ${diffDays} días`;
    }
  }

  onFilterChange(): void {
    this.loadNotifications();
  }
}