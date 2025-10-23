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
   
    const serviceMap ={
      unread: () => this.notificacionService.findUnread(),
      read: () => this.notificacionService.findAll(),
      all:() => this.notificacionService.findAll()
    }

    const serviceCall = serviceMap[this.filter]();

  serviceCall.subscribe({
    next: (notifications) => {
      this.notifications = this.filter === 'read'
        ? notifications.filter(n => n.leida)
        : notifications;

      this.unreadCount = this.notifications.filter(n => !n.leida).length;
      this.loading = false;
    },
    error: (error) => {
      console.error('Error loading notifications:', error);
      this.loading = false;
      this.customAlertService.showError('Error', 'No se pudieron cargar las notificaciones');
    },
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

    const iconMap: Record<string, string> ={
      VENCIMIENTO_MEMBRESIA: 'fas fa-exclamation-triangle text-warning',
      PAGO_PENDIENTE: 'fas fa-dollar-sign text-info',
      BIENVENIDA: 'fas fa-hand-wave text-success',
    };
    return iconMap[type]  || 'fas fa-bell text-secondary'

  }

  getRelativeTime(date: Date): string {
    const now = new Date();
    const notificationDate = new Date(date);
    const diffDays = Math.ceil(Math.abs(+now - +notificationDate) / 100 * 60 * 60 * 24);

    const rules =[
      {condition: diffDays === 0, message: 'Hoy' },
      {condition: diffDays === 1, message: 'Ayer'},
      {condition: diffDays < 7, message: `Hace ${diffDays} días`},
      {condition: diffDays < 30, message: `Hace ${Math.floor(diffDays / 7)} semana${Math.floor(diffDays / 7) > 1 ? 's': ''}`},
      {condition: diffDays < 30, message: `Hace ${Math.floor(diffDays / 30)} mes${Math.floor(diffDays / 30) > 1 ? 'es': ''}`}
    ];
    return rules.find(r=> r.condition)?.message || `Hace ${Math.floor(diffDays / 365)} año${Math.floor(diffDays / 365) > 1 ? 's' : ''}`;
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