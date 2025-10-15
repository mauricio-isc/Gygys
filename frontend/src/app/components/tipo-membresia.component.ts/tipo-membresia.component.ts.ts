import { Component } from '@angular/core';
import { TipoMembresiaService } from '../../services/tipo.membresia.service';
import { TipoMembresia } from '../../models/tipo-membresia.model';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-tipo-membresia.component.ts',
  imports: [],
  templateUrl: './tipo-membresia.component.ts.html',
  styleUrl: './tipo-membresia.component.ts.scss'
})
export class TipoMembresiaComponentTs {
  tipos: TipoMembresia[] = [];
  loading = false;
  showForm = false;
  editingTipo: TipoMembresia | null = null;

  formData: Omit<TipoMembresia, 'id'> = {
    nombre: '',
    descripcion: '',
    duracionDias: 30,
    precio: 0,
    activo: true
  };

  constructor(private tipoMembresiaService: TipoMembresiaService){}

  ngOnInit():void{
    this.loadTipos();
  }

  loadTipos():void{
    this.loading = true;
    this.tipoMembresiaService.findAll().subscribe({
      next: (tipos) =>{
        this.tipos = tipos;
        this.loading = false;
      },
      error: () =>{
        this.loading = false;
        Swal.fire('Error', 'Error al cargar los tipos de membresia', 'error');
      }
    });
  }


  openCreateForm(): void{
    this.editingTipo = null;
    this.formData = {
      nombre: '',
      descripcion: '',
      duracionDias: 30,
      precio: 0,
      activo: true
    };
    this.showForm = true;
  }


}
