import { Component, OnInit } from '@angular/core';
import { MembresiaService } from '../../services/membresia.service';
import { Membresia } from '../../models/membresia.model';
import Swal from 'sweetalert2';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-memberships',
  templateUrl: './memberships.component.html',
  styleUrls: ['./memberships.component.scss'],
  standalone: true,
  imports:[
    CommonModule
  ]
})
export class MembershipsComponent implements OnInit {
  memberships: Membresia[] = [];
  loading = false;

  constructor(private membresiaService: MembresiaService) {}

  ngOnInit(): void {
    this.loadMemberships();
  }

  loadMemberships(): void {
    this.loading = true;
    this.membresiaService.findAll().subscribe({
      next: (memberships) => {
        this.memberships = memberships;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading memberships:', error);
        this.loading = false;
      }
    });
  }

  loadExpiringMemberships(): void {
    this.loading = true;
    this.membresiaService.findExpiringMemberships().subscribe({
      next: (memberships) => {
        this.memberships = memberships;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading expiring memberships:', error);
        this.loading = false;
      }
    });
  }

  activateMembership(): void {
    // Aquí se implementaría el diálogo para activar membresía
    Swal.fire({
      title: 'Activar Membresía',
      text: 'Funcionalidad en desarrollo',
      icon: 'info'
    });
  }

  formatCurrency(value: number): string {
    return new Intl.NumberFormat('es-CO', {
      style: 'currency',
      currency: 'COP'
    }).format(value);
  }

  getStatusBadgeClass(status: string): string {
    switch (status) {
      case 'ACTIVA':
        return 'bg-success';
      case 'VENCIDA':
        return 'bg-danger';
      case 'POR_VENCER':
        return 'bg-warning';
      default:
        return 'bg-secondary';
    }
  }
}