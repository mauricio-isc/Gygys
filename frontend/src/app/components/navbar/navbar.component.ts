import { Component, OnInit, OnDestroy } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { NotificacionService } from '../../services/notificacion.service';
import { Notificacion } from '../../models/notificacion.model';
import { Router, RouterModule } from '@angular/router';
import { Subscription } from 'rxjs';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss'],
  imports: [CommonModule, RouterModule]
})
export class NavbarComponent implements OnInit, OnDestroy {
  currentUser: any;
  notifications: Notificacion[] = [];
  unreadNotifications = 0;
  isSearchFocused = false;
  isLoading = false;
  
  private subscriptions = new Subscription();

  constructor(
    private authService: AuthService,
    private notificacionService: NotificacionService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadUserData();
    this.loadNotifications();
    this.setupAutoRefresh();
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

  private loadUserData(): void {
    this.subscriptions.add(
      this.authService.currentUser$.subscribe(user => {
        this.currentUser = user;
      })
    );
  }

  private loadNotifications(): void {
    this.isLoading = true;
    
    this.subscriptions.add(
      this.notificacionService.findUnread().subscribe({
        next: (notificaciones) => {
          this.notifications = notificaciones;
          this.unreadNotifications = notificaciones.filter(n => !n.leida).length;
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Error cargando notificaciones:', error);
          this.isLoading = false;
        }
      })
    );
  }

  private setupAutoRefresh(): void {
    const refreshInterval = setInterval(() => {
      this.loadNotifications();
    }, 30000);

    this.subscriptions.add({
      unsubscribe: () => clearInterval(refreshInterval)
    });
  }

  onSearchFocus(): void {
    this.isSearchFocused = true;
  }

  onSearchBlur(): void {
    this.isSearchFocused = false;
  }

  markAsRead(notification: Notificacion, event: Event): void {
    event.preventDefault();
    event.stopPropagation();

    this.subscriptions.add(
      this.notificacionService.markAsRead(notification.id).subscribe({
        next: () => {
          this.notifications = this.notifications.filter(n => n.id !== notification.id);
          this.unreadNotifications = this.notifications.filter(n => !n.leida).length;
        },
        error: (error) => {
          console.error('Error marcando notificación como leída:', error);
        }
      })
    );
  }

  markAllAsRead(event: Event): void {
    event.preventDefault();
    
    const markPromises = this.notifications.map(notification => 
      this.notificacionService.markAsRead(notification.id).toPromise()
    );

    Promise.all(markPromises).then(() => {
      this.notifications = [];
      this.unreadNotifications = 0;
    }).catch(error => {
      console.error('Error marcando todas como leídas:', error);
    });
  }

  navigateFromNotification(notification: Notificacion, event: Event): void {
    event.preventDefault();
    
    this.markAsRead(notification, event);
    
    // Navegación basada en el tipo de notificación
    switch (notification.tipoNotificacion) {
      case 'VENCIMIENTO_MEMBRESIA':
        this.router.navigate(['/memberships']);
        break;
      case 'PAGO_PENDIENTE':
        this.router.navigate(['/payments']);
        break;
      case 'BIENVENIDA':
      case 'GENERAL':
      default:
        this.router.navigate(['/members', notification.miembroId]);
        break;
    }
  }

  logout(event: Event): void {
    event.preventDefault();
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  // Obtener icono según el tipo de notificación REAL
  getNotificationIcon(tipoNotificacion: string): string {
    const iconMap: { [key: string]: string } = {
      'VENCIMIENTO_MEMBRESIA': 'fas fa-calendar-exclamation text-warning',
      'PAGO_PENDIENTE': 'fas fa-exclamation-triangle text-danger',
      'BIENVENIDA': 'fas fa-hand-wave text-success',
      'GENERAL': 'fas fa-info-circle text-info'
    };
    
    return iconMap[tipoNotificacion] || 'fas fa-bell text-primary';
  }

  // Obtener clase de color según el tipo
  getNotificationBadgeClass(tipoNotificacion: string): string {
    const badgeMap: { [key: string]: string } = {
      'VENCIMIENTO_MEMBRESIA': 'bg-warning',
      'PAGO_PENDIENTE': 'bg-danger',
      'BIENVENIDA': 'bg-success',
      'GENERAL': 'bg-info'
    };
    
    return badgeMap[tipoNotificacion] || 'bg-primary';
  }

  // Formatear fecha de notificación
  formatNotificationDate(fechaEnvio: Date): string {
    const date = new Date(fechaEnvio);
    const now = new Date();
    const diffMs = now.getTime() - date.getTime();
    const diffMins = Math.floor(diffMs / 60000);
    const diffHours = Math.floor(diffMins / 60);
    const diffDays = Math.floor(diffHours / 24);

    if (diffMins < 1) return 'Ahora mismo';
    if (diffMins < 60) return `Hace ${diffMins} min`;
    if (diffHours < 24) return `Hace ${diffHours} h`;
    if (diffDays === 1) return 'Ayer';
    return `Hace ${diffDays} días`;
  }

  // Obtener texto descriptivo del tipo
  getNotificationTypeText(tipoNotificacion: string): string {
    const typeMap: { [key: string]: string } = {
      'VENCIMIENTO_MEMBRESIA': 'Vencimiento',
      'PAGO_PENDIENTE': 'Pago Pendiente',
      'BIENVENIDA': 'Bienvenida',
      'GENERAL': 'General'
    };
    
    return typeMap[tipoNotificacion] || 'Notificación';
  }
}