import { Membresia } from './membresia.model';

export interface Miembro {
    id: number;
    nombre: string;
    apellido: string;
    email: string;
    telefono?: string;
    fechaNacimiento?: Date;
    direccion?: string;
    fechaRegistro: Date;
    activo: boolean;
    notas?: string;
    documentoIdentidad: string;
    genero?: 'MASCULINO' | 'FEMENINO' | 'OTRO';
    fotourl?: string;
    nombreCompleto: string;
    tieneMembresiaActiva: boolean;
    membresiaActiva?: Membresia;
}


export interface MiembroRequest {
    nombre: string;
    apellido: string;
    email: string;
    telefono?: string;
    fechaNacimiento?: Date;
    direccion?: string;
    documentoIdentidad: string;
    genero?: 'MASCULINO' | 'FEMENINO' | 'OTRO';
    notas?: string; 
}

export interface MiembroResponse {
  //cualquier campo adicional que necesite 
    id: number;
    nombre: string;
    apellido: string;
    email: string;
    telefono?: string;
    fechaNacimiento?: Date;
    direccion?: string;
    fechaRegistro: Date;
    activo: boolean;
    notas?: string;
    documentoIdentidad: string;
    genero?: 'MASCULINO' | 'FEMENINO' | 'OTRO';
    fotourl?: string;
    nombreCompleto: string;
    tieneMembresiaActiva: boolean;
    membresiaActiva?: Membresia;
}