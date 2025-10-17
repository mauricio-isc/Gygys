import { CommonModule } from '@angular/common';
import { Component, EventEmitter, HostListener, OnInit, Output } from '@angular/core';
import { Router, RouterModule } from '@angular/router';

interface MenuItem {
  title: string;
  icon: string;
  route: string;
  active?: boolean;
  hasNotification?: boolean;
  submenu?: SubMenuItem[];
}

interface SubMenuItem {
  title: string;
  route: string;
}

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss'],
  standalone: true,
  imports: [CommonModule, RouterModule],
})
export class SidebarComponent implements OnInit {
  @Output() sideBarToggled = new EventEmitter<Boolean>();

  menuItems: MenuItem[] = [
    {
      title: 'Dashboard',
      icon: 'fas fa-tachometer-alt',
      route: '/dashboard',
    },
    {
      title: 'Miembros',
      icon: 'fas fa-users',
      route: '/members',
    },
    {
      title: 'Membresías',
      icon: 'fas fa-id-card',
      route: '/memberships',
    },
    {
      title: 'Crear membresias',
      icon: 'fa-solid fa-plus',
      route: '/tipos-membresia',
    },

    {
      title: 'Notificaciones',
      icon: 'fas fa-bell',
      route: '/notifications',
    },
  ];

  isCollapsed = false;
  isMobileOpen = false;
  isMobile = false;

  constructor(private router: Router) {}

  ngOnInit(): void {
    this.updateActiveMenu();
  }

  updateActiveMenu(): void {
    const currentRoute = this.router.url;
    this.menuItems.forEach((item) => {
      item.active = currentRoute.includes(item.route);

      if (item.submenu) {
        const hasActivedChild = item.submenu.some((subItem) =>
          currentRoute.startsWith(subItem.route),
        );
        if (hasActivedChild) {
          item.active = true;
        }
      }
    });
  }

  navigateTo(route: string): void {
    this.router.navigate([route]);
    if (this.isMobile) {
      this.updateActiveMenu();
    }
  }

  toggleSideBar(): void {
    if (this.isMobile) {
      this.isMobileOpen = !this.isMobileOpen;
      this.sideBarToggled.emit(false);
    } else {
      this.isCollapsed = !this.isCollapsed;
      this.sideBarToggled.emit(this.isCollapsed);
    }
  }

  closeMobileSideBar(): void {
    this.isMobileOpen = false;
  }


}
