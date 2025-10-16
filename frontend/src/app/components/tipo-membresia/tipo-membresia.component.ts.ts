import { Component } from '@angular/core';
import { TipoMembresiaService } from '../../services/tipo.membresia.service';
import { TipoMembresia } from '../../models/tipo-membresia.model';
import Swal from 'sweetalert2';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-tipo-membresia.component.ts',
  imports: [CommonModule, FormsModule, ],
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

  openEditForm(tipo: TipoMembresia): void{
    this.editingTipo = tipo;
    this.formData ={
      nombre: tipo.nombre,
      descripcion: tipo.descripcion,
      duracionDias: tipo.duracionDias,
      precio: tipo.precio,
      activo: tipo.activo
    };
    this.showForm = true;
  }

  closeForm(): void{
    this.showForm = false;
    this.editingTipo = null;
  }

  onSubmit(): void{
    if(this.editingTipo){
      this.updateTipo();
    }else{
      this.createTipo();
    }
  }

  createTipo(): void{
    this.tipoMembresiaService.create(this.formData).subscribe({
      next: () => {
        Swal.fire('Exito', 'Tipo de membresia creado correctamente', 'success');
        this.closeForm();
        this.loadTipos();
      },
      error: () => {
        Swal.fire('Error', 'Error al crear el tipo de membresia', 'error');
      }
    });
  }

  updateTipo(): void{
    if(!this.editingTipo) return;

    this.tipoMembresiaService.update(this.editingTipo.id, this.formData).subscribe({
      next: () => {
        Swal.fire('Exito', 'Tipo de membresia actualizada correctamente', 'success');
        this.closeForm();
        this.loadTipos();
      },
      error: () => {
        Swal.fire('Error', 'Error al actualizar el tipo de membresia', 'error');
      }
    });
  }

  deleteTipo(tipo: TipoMembresia): void{
    Swal.fire({
      title: '¿Estás Seguro?',
      text: `¿Deseas eliminar el tipo de membresía "${tipo.nombre}"?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Sí, eliminar',
      cancelButtonText: 'Cancelar'
    }).then((result) => {
      if(result.isConfirmed){
        this.tipoMembresiaService.delete(tipo.id).subscribe({
          next: () => {
            Swal.fire('Eliminado', 'Tipo de membresia eliminada correctamente', 'success');
            this.loadTipos();
          },
          error: () =>{
            Swal.fire('Error', 'Error al eliminar el tipo de membresia', 'error');
          }
        });
      }
    });
  }

  toggleActivo(tipo: TipoMembresia): void{
    const request: Partial<TipoMembresia> ={ 
      nombre: tipo.nombre,
      descripcion: tipo.descripcion,
      duracionDias:tipo.duracionDias,
      precio: tipo.precio,
      activo: !tipo.activo
    };

    this.tipoMembresiaService.update(tipo.id, request).subscribe({
      next: () => {
        tipo.activo = !tipo.activo;
        Swal.fire('Exito', `Tipo de membresia ${tipo.activo ? 'activado' : 'desactivado'} correctamente`, 'success');
      },
      error: () =>{
        Swal.fire('Error', 'Error al cambiar el estado', 'error');
      }
    });
  }

  //metodo para formatear la duracion
  getDuracionFormateada(duracionDias: number): string {
    if(duracionDias < 30){
        return duracionDias + 'dias';
    }else if(duracionDias < 365){
        const meses = Math.floor(duracionDias / 30);
        const diasRestantes = duracionDias % 30;
        if(diasRestantes === 0){
          return meses + ' mes ' + (meses > 1 ? 'es' : '');
        }else{
          return meses + 'mes' + (meses > 1 ? 'es' : '') + ' y ' + diasRestantes + ' día ' + (diasRestantes > 1 ? 's' : ''); 
        }
    }else{
      const años = Math.floor(duracionDias / 365);
      const diasRestantes = duracionDias % 365;
      if(diasRestantes === 0){
        return años + ' año' + (años > 1 ? 's' : '');
      }else{
        const meses = Math.floor(diasRestantes / 30);
        const diasFinales = diasRestantes % 30;
        let resultado = años + ' año' + (años > 1 ? 's' : '');
        if(meses > 0 ){
          resultado += ' y ' + meses + ' mes' + (meses > 1 ? 'es' : '');
        }
        if(diasFinales > 0 ){
          resultado += ' y' + diasFinales + ' día' + (diasFinales > 1 ? 's' : '');
        }
        return resultado;
      }
    }
  }
}
