import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Router, RouterModule } from '@angular/router';

interface MenuItem {
  title: string;
  icon: string;
  route: string;
  active?: boolean;
}

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss'],
    standalone: true,
  imports: [
    CommonModule,
    RouterModule  
  ]
})
export class SidebarComponent implements OnInit {
  menuItems: MenuItem[] = [
    {
      title: 'Dashboard',
      icon: 'fas fa-tachometer-alt',
      route: '/dashboard'
    },
    {
      title: 'Miembros',
      icon: 'fas fa-users',
      route: '/members'
    },
    {
      title: 'Membresías',
      icon: 'fas fa-id-card',
      route: '/memberships'
    },
        {
      title: 'Crear membresias',
      icon: 'fa-solid fa-plus',
      route:'/tipos-membresia'
    },

    {
      title: 'Notificaciones',
      icon: 'fas fa-bell',
      route: '/notifications'
    }

  ];

  constructor(private router: Router) {}

  ngOnInit(): void {
    this.updateActiveMenu();
  }

  updateActiveMenu(): void {
    const currentRoute = this.router.url;
    this.menuItems.forEach(item => {
      item.active = currentRoute.includes(item.route);
    });
  }

  navigateTo(route: string): void {
    this.router.navigate([route]);
    this.updateActiveMenu();
  }
}