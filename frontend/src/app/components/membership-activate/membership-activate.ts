import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MiembroService } from '../../services/miembro.service';
import { TipoMembresiaService } from '../../services/tipo.membresia.service';
import { Miembro } from '../../models/miembro.model';
import { TipoMembresia } from '../../models/tipo-membresia.model';
import { debounceTime, distinctUntilChanged, Subject, switchMap } from 'rxjs';

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
  miembrosFiltrados: Miembro[] = [];
  tiposMembresia: TipoMembresia[] = [];
  
  selectedMiembroId: number | null = null;
  selectedMiembro: Miembro | null = null;
  selectedTipoMembresiaId: number | null = null;
  precioPagado: number = 0;
  metodoPago: string = 'EFECTIVO';
  referenciaPago: string = '';
  notas: string = '';
  
  // Búsqueda
  searchTerm: string = '';
  showDropdown: boolean = false;
  private searchTerms = new Subject<string>();
  
  loadingMiembros = false;
  loadingTipos = false;
  submitting = false;

  precioSugerido: number = 0;
  
  // Opciones de métodos de pago
  metodosPago = [
    { value: 'EFECTIVO', label: 'Efectivo' },
    { value: 'TARJETA', label: 'Tarjeta' },
    { value: 'TRANSFERENCIA', label: 'Transferencia' },
    { value: 'OTRO', label: 'Otro' }
  ];

  constructor(
    private miembroService: MiembroService,
    private tipoMembresiaService: TipoMembresiaService
  ) {}

  ngOnInit(): void {
    this.setupSearch();
    this.loadData();
  }

  setupSearch(): void {
    this.searchTerms.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      switchMap(term => {
        if (term.length < 2) {
          this.miembrosFiltrados = this.miembros;
          return [];
        }
        return this.miembroService.searchMiembros(term);
      })
    ).subscribe({
      next: (response) => {
        this.miembrosFiltrados = response.content || response;
        this.showDropdown = true;
      },
      error: (error) => {
        console.error('Error searching miembros:', error);
        this.miembrosFiltrados = this.miembros;
      }
    });
  }

  loadData(): void {
    this.loadMiembros();
    this.loadTiposMembresia();
  }

  loadMiembros(): void {
    this.loadingMiembros = true;
    this.miembroService.findAll().subscribe({
      next: (response) => {
        this.miembros = response.content || response;
        this.miembrosFiltrados = this.miembros;
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

  onSearchChange(term: string): void {
    this.searchTerm = term;
    if (term.length < 2) {
      this.miembrosFiltrados = this.miembros;
      this.showDropdown = true;
      return;
    }
    this.searchTerms.next(term);
  }

  selectMiembro(miembro: Miembro): void {
    this.selectedMiembro = miembro;
    this.selectedMiembroId = miembro.id;
    this.searchTerm = `${miembro.nombre} ${miembro.apellido} - ${miembro.email}`;
    this.showDropdown = false;
  }

  clearSelection(): void {
    this.selectedMiembro = null;
    this.selectedMiembroId = null;
    this.searchTerm = '';
    this.miembrosFiltrados = this.miembros;
    this.showDropdown = true;
  }

  onFocus(): void {
    if (this.searchTerm === '' && this.miembrosFiltrados.length > 0) {
      this.showDropdown = true;
    }
  }

  onBlur(): void {
    setTimeout(() => {
      this.showDropdown = false;
    }, 200);
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
      precioPagado: this.precioPagado,
      metodoPago: this.metodoPago,
      referenciaPago: this.referenciaPago || undefined,
      notas: this.notas || undefined
    };

    this.membershipActivated.emit(activationData);
  }

  onCancel(): void {
    this.cancel.emit();
    this.resetForm();
  }

  resetForm(): void {
    this.selectedMiembroId = null;
    this.selectedMiembro = null;
    this.selectedTipoMembresiaId = null;
    this.precioPagado = 0;
    this.precioSugerido = 0;
    this.metodoPago = 'EFECTIVO';
    this.referenciaPago = '';
    this.notas = '';
    this.searchTerm = '';
    this.miembrosFiltrados = this.miembros;
    this.showDropdown = false;
    this.submitting = false;
  }

  isFormValid(): boolean {
    return !!this.selectedMiembroId && 
           !!this.selectedTipoMembresiaId && 
           this.precioPagado > 0;
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

  getMetodoPagoLabel(metodo: string): string {
    const metodoEncontrado = this.metodosPago.find(m => m.value === metodo);
    return metodoEncontrado ? metodoEncontrado.label : metodo;
  }
}