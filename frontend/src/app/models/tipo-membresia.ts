export interface TipoMembresia{
    id: number;
    nombre: string;
    descripcion: string;
    duracionDias: number;
    precio: number;
    activo: boolean;
    fechaCreacion?: Date;
}