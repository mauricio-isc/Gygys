import { Component, OnInit } from '@angular/core';
import { MiembroService } from '../../services/miembro.service';
import { Miembro } from '../../models/miembro.model';
import { CustomAlertService } from '../../services/custom-alert.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
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
  deletingMembers: Set<number> = new Set();

  constructor(
    private miembroService: MiembroService, 
    private customAlertService: CustomAlertService
  ) {}

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
      error: () => (this.loading = false)
    });
  }

  searchMembers(): void {
    this.currentPage = 0;

    if (this.searchTerm.trim()) {
      this.loading = true;
      this.miembroService.search(this.searchTerm, this.currentPage, this.pageSize).subscribe({
        next: (response: Page<Miembro>) => {
          this.members = response.content;
          this.totalElements = response.totalElements;
          this.loading = false;
        },
        error: () => (this.loading = false)
      });
    } else {
      this.loadMembers();
    }
  }

  onPageChange(page: number): void {
    this.currentPage = page;
    if(this.searchTerm.trim()){
      this.searchMembers();
    }else{
      this.loadMembers();
    }
  }

  deleteMember(member: Miembro): void {
    this.customAlertService.showConfirm(
      'Eliminar Miembro',
      `¿Estás seguro de que deseas eliminar al miembro ${member.nombreCompleto}? Esta acción no se puede deshacer.`,
      'Sí, eliminar',
      'Cancelar'
    ).subscribe((confirmed) => {
      if(confirmed) {
        this.deletingMembers.add(member.id);
        
        this.miembroService.delete(member.id).subscribe({
          next: () => {
            this.deletingMembers.delete(member.id);
            
            this.customAlertService.showSuccess(
              'Eliminado',
              `El miembro ${member.nombreCompleto} ha sido eliminado correctamente.`
            );
            this.loadMembers();
          },
          error: (error) => {
            this.deletingMembers.delete(member.id);
            
            console.error('Error al eliminar:', error);
            
            let errorMessage = 'No se pudo eliminar el miembro. Intente de nuevo.';
            
            if (error.status === 404) {
              errorMessage = 'El miembro no fue encontrado.';
            } else if (error.status === 409) {
              errorMessage = 'No se puede eliminar el miembro porque tiene registros asociados.';
            } else if (error.status >= 500) {
              errorMessage = 'Error del servidor. Por favor, intente más tarde.';
            }
            
            this.customAlertService.showError('Error', errorMessage);
          }
        });
      }
    });
  }

  isDeleting(memberId: number): boolean {
    return this.deletingMembers.has(memberId);
  }

  get pages(): number[] {
    const totalPages = Math.ceil(this.totalElements / this.pageSize);
    return Array.from({ length: totalPages }, (_, i) => i);
  }
}