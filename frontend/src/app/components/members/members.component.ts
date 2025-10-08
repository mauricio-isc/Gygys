import { Component, OnInit } from '@angular/core';
import { MiembroService } from '../../services/miembro.service';
import { Miembro } from '../../models/miembro.model';
import Swal from 'sweetalert2';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

// Interfaz de paginación
export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number; // página actual
  size: number;   // tamaño de página
}

@Component({
  selector: 'app-members',
  templateUrl: './members.component.html',
  styleUrls: ['./members.component.scss'],
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule]
})
export class MembersComponent implements OnInit {
  members: Miembro[] = [];
  loading = false;
  searchTerm = '';
  currentPage = 0;
  pageSize = 10;
  totalElements = 0;

  constructor(private miembroService: MiembroService) {}

  ngOnInit(): void {
    this.loadMembers();
  }

  loadMembers(): void {
    this.loading = true;
    this.miembroService.findAll(this.currentPage, this.pageSize).subscribe({
      next: (response: Page<Miembro>) => {
        this.members = response.content;
        this.totalElements = response.totalElements;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading members:', error);
        this.loading = false;
      }
    });
  }

  searchMembers(): void {
    if (this.searchTerm.trim()) {
      this.loading = true;
      this.miembroService.search(this.searchTerm, this.currentPage, this.pageSize).subscribe({
        next: (response: Page<Miembro>) => {
          this.members = response.content;
          this.totalElements = response.totalElements;
          this.loading = false;
        },
        error: (error) => {
          console.error('Error searching members:', error);
          this.loading = false;
        }
      });
    } else {
      this.loadMembers();
    }
  }

  onPageChange(page: number): void {
    this.currentPage = page;
    this.loadMembers();
  }

  deleteMember(member: Miembro): void {
    Swal.fire({
      title: '¿Estás seguro?',
      text: `¿Deseas eliminar al miembro ${member.nombreCompleto}?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Sí, eliminar',
      cancelButtonText: 'Cancelar'
    }).then((result) => {
      if (result.isConfirmed) {
        this.miembroService.delete(member.id).subscribe({
          next: () => {
            Swal.fire('Eliminado', 'El miembro ha sido eliminado correctamente', 'success');
            this.loadMembers();
          },
          error: (error) => {
            console.error('Error deleting member:', error);
            Swal.fire('Error', 'No se pudo eliminar el miembro', 'error');
          }
        });
      }
    });
  }

  get pages(): number[] {
    const totalPages = Math.ceil(this.totalElements / this.pageSize);
    return Array.from({ length: totalPages }, (_, i) => i);
  }
}
