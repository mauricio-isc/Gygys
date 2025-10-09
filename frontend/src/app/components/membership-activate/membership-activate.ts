import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MiembroService } from '../../services/miembro.service';
import { TipoMembresiaService } from '../../services/tipo.membresia.service';
import { Miembro } from '../../models/miembro.model';
import { TipoMembresia } from '../../models/tipo-membresia.model';


@Component({
  selector: 'app-membership-activate',
  templateUrl: './membership-activate.html',
  styleUrl: './membership-activate.scss',
  standalone: true,
  imports: [CommonModule, FormsModule]
})
export class MembershipActivationComponent implements OnInit {
  @Output() membershipActivated = new EventEmitter<any>();
  @Output() cancel = new EventEmitter<void>();

  miembros: Miembro[] = [];
  tiposMembresia: TipoMembresia[] = [];
  
  selectedMiembroId: number | null = null;
  selectedTipoMembresiaId: number | null = null;
  precioPagado: number = 0;
  
  loadingMiembros = false;
  loadingTipos = false;
  submitting = false;

  precioSugerido: number = 0;

  constructor(
    private miembroService: MiembroService,
    private tipoMembresiaService: TipoMembresiaService
  ) {}

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.loadMiembros();
    this.loadTiposMembresia();
  }

  loadMiembros(): void {
    this.loadingMiembros = true;
    this.miembroService.findAll().subscribe({
      next: (response) => {
          this.miembros = response.content;
          this.loadingMiembros = false;
      },
      error: (error) => {
        console.error('Error loading miembros:', error);
        this.loadingMiembros = false;
      }
    });
  }

  loadTiposMembresia(): void {
    this.loadingTipos = true;
    this.tipoMembresiaService.findAll().subscribe({
      next: (tipos) => {
        this.tiposMembresia = tipos.filter(t => t.activo);
        this.loadingTipos = false;
      },
      error: (error) => {
        console.error('Error loading tipos membresía:', error);
        this.loadingTipos = false;
      }
    });
  }

  onTipoMembresiaChange(): void {
    if (this.selectedTipoMembresiaId) {
      const tipoSeleccionado = this.tiposMembresia.find(t => t.id === this.selectedTipoMembresiaId);
      if (tipoSeleccionado) {
        this.precioSugerido = tipoSeleccionado.precio;
        this.precioPagado = tipoSeleccionado.precio;
      }
    } else {
      this.precioSugerido = 0;
      this.precioPagado = 0;
    }
  }

  onSubmit(): void {
    if (!this.isFormValid()) {
      return;
    }

    this.submitting = true;

    const activationData = {
      miembroId: this.selectedMiembroId!,
      tipoMembresiaId: this.selectedTipoMembresiaId!,
      precioPagado: this.precioPagado
    };

    this.membershipActivated.emit(activationData);
  }

  onCancel(): void {
    this.cancel.emit();
    this.resetForm();
  }

  resetForm(): void {
    this.selectedMiembroId = null;
    this.selectedTipoMembresiaId = null;
    this.precioPagado = 0;
    this.precioSugerido = 0;
    this.submitting = false;
  }

  isFormValid(): boolean {
    return !!this.selectedMiembroId && 
           !!this.selectedTipoMembresiaId && 
           this.precioPagado > 0;
  }

  getSelectedMiembro(): Miembro | undefined {
    return this.miembros.find(m => m.id === this.selectedMiembroId);
  }

  getSelectedTipoMembresia(): TipoMembresia | undefined {
    return this.tiposMembresia.find(t => t.id === this.selectedTipoMembresiaId);
  }

  formatCurrency(value: number): string {
    return new Intl.NumberFormat('es-CO', {
      style: 'currency',
      currency: 'COP'
    }).format(value);
  }
}