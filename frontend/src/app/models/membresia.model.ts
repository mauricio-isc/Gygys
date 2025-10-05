import { Miembro } from "./miembro.model";
import {  TipoMembresia } from "./tipo-membresia.model";

export interface Membresia {
  id: number;
  miembroId: number;
  nombreMiembro: string;
  tipoMembresiaId: number;
  nombreTipoMembresia: string;
  fechaInicio: Date;
  fechaFin: Date;
  estado: 'ACTIVA' | 'INACTIVA' | 'VENCIDA' | 'CANCELADA';
  precioPagado: number;
  fechaCreacion: Date;
  fechaActualizacion: Date;
  creadoPor: number;
  nombreCreador: string;
  diasRestantes: number;
  vencida: boolean;
  vencePronto: boolean;
  diasParaVencimiento?: number;
}

export interface MembresiaRequest {
  miembroId: number;
  tipoMembresiaId: number;
  fechaInicio: Date;
  precioPagado: number;
  notas?: string;
}

export interface ActivateMembershipRequest {
  miembroId: number;
  tipoMembresiaId: number;
  precioPagado: number;
}