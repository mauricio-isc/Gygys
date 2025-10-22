import { Component, OnInit, HostListener } from '@angular/core';
import { AuthService } from './services/auth.service';
import { Router, RouterModule } from '@angular/router';
import { NavbarComponent } from './components/navbar/navbar.component';
import { SidebarComponent } from './components/sidebar/sidebar.component';
import { CommonModule } from '@angular/common';
import { CustomAlertComponent } from "./components/custom-alert/custom-alert.component";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
  standalone: true,
  imports: [NavbarComponent, SidebarComponent, RouterModule, CommonModule, CustomAlertComponent],
})
export class AppComponent implements OnInit {
  sidebarCollapsed = false;
  isMobileSidebarOpen = false;
  title = 'Gym Management System';
  isLoggedIn = false;

  constructor(
    private authService: AuthService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.authService.currentUser$.subscribe((user) => {
      this.isLoggedIn = !!user;
    });
    this.checkScreenSize();
  }

  @HostListener('window:resize', ['$event'])
  onResize(event: any) {
    this.checkScreenSize();
  }

  private checkScreenSize(): void {
    // Cerrar sidebar móvil cuando se cambia a desktop
    if (window.innerWidth > 768 && this.isMobileSidebarOpen) {
      this.isMobileSidebarOpen = false;
    }
  }

  onSidebarToggled(event: any): void {
    if (typeof event === 'boolean') {
      this.sidebarCollapsed = event;
    } else {
      // Si es un evento del DOM, manejar según el dispositivo
      if (window.innerWidth <= 768) {
        this.isMobileSidebarOpen = !this.isMobileSidebarOpen;
      } else {
        this.sidebarCollapsed = !this.sidebarCollapsed;
      }
    }
  }

  closeMobileSidebar(): void {
    this.isMobileSidebarOpen = false;
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  // Método para abrir/cerrar sidebar en móvil desde navbar
  toggleMobileSidebar(): void {
    this.isMobileSidebarOpen = !this.isMobileSidebarOpen;
  }
}