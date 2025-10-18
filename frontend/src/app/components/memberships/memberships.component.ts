import { Component, OnInit } from '@angular/core';
import { MembresiaService } from '../../services/membresia.service';
import { MiembroService } from '../../services/miembro.service';
import { Membresia } from '../../models/membresia.model';
import Swal from 'sweetalert2';
import { CommonModule } from '@angular/common';
import { MembershipActivationComponent } from '../membership-activate/membership-activate';
import { RouterModule } from '@angular/router';
import { Miembro } from '../../models/miembro.model';
import { FormsModule } from '@angular/forms';

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

@Component({
  selector: 'app-memberships',
  templateUrl: './memberships.component.html',
  styleUrls: ['./memberships.component.scss'],
  standalone: true,
  imports: [
    CommonModule,
    MembershipActivationComponent,
    FormsModule,
    RouterModule
]
})

export class MembershipsComponent implements OnInit {
  memberships: Membresia[] = [];
  loading = false;
  showActivationModal = false;
  members: Miembro[] = [];
  searchTerm = '';
  currentPage = 0;
  pageSize = 10;
  totalElements = 0;
  
  constructor(private membresiaService: MembresiaService, private miembroService: MiembroService) {}

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

  showActivationForm(): void {
    this.showActivationModal = true;  
  }

  activateMembership(activationData: any): void {
    this.membresiaService.activateMembership(
      activationData.miembroId,
      activationData.tipoMembresiaId,
      activationData.precioPagado
    ).subscribe({
      next: (membresia) => {
        this.showActivationModal = false; 
        Swal.fire({
          title: '¡Membresia Activada!',
          text: `La membresia ha sido activada exitosamente para el usuario ${membresia.nombreMiembro}`,
          icon: 'success',
          confirmButtonText: 'Aceptar'
        });
        this.loadMemberships();
      },
      error: (error) => {
        console.error('Error activating membership:', error);
        this.showActivationModal = false; 
        Swal.fire({
          title: 'Error',
          text: error.error?.message || 'Error al activar la membresía',
          icon: 'error',
          confirmButtonText: 'Aceptar'
        });
      }
    });
  }

  onActivationCancel(): void {
    this.showActivationModal = false; 
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

searchMembers(): void {
  this.currentPage = 0;

  if (this.searchTerm.trim()) {
    this.loading = true;
    this.miembroService.search(this.searchTerm, this.currentPage, this.pageSize).subscribe({
      next: (response: Page<Miembro>) => {
        console.log('Resultados de búsqueda:', response);
        this.members = response.content; // ✅ correcto
        this.totalElements = response.totalElements;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error en búsqueda:', error);
        this.loading = false;
      }
    });
  } else {
    this.loadMembers();
  }
}

      loadMembers(): void {
        this.loading = true;
        this.miembroService.findAll(this.currentPage, this.pageSize).subscribe({
          next: (response: Page<Miembro>) => {
            this.members = response.content;
            this.totalElements = response.totalElements;
            this.loading = false;
          },
          error: () => (this.loading = false)
        });
      }
}