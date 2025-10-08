import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { User } from '../../models/auth.model';
import { Router, RouterModule } from '@angular/router';
import { NotificacionService } from '../../services/notificacion.service';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss'],
    imports: [
    CommonModule,
    RouterModule
  ]
})
export class NavbarComponent implements OnInit {
  currentUser: User | null = null;
  unreadNotifications = 0;

  constructor(
    private authService: AuthService,
    private router: Router,
    private notificacionService: NotificacionService
  ) {}

  ngOnInit(): void {
    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
    });

    this.loadNotificationStats();
    // Actualizar estadísticas cada 30 segundos
    setInterval(() => {
      this.loadNotificationStats();
    }, 30000);
  }

  loadNotificationStats(): void {
    this.notificacionService.getNotificationStats().subscribe({
      next: (stats) => {
        this.unreadNotifications = stats.unreadCount || 0;
      },
      error: (error) => {
        console.error('Error loading notification stats:', error);
      }
    });
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}