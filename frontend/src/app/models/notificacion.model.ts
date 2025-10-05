import { Miembro } from './miembro.model';

export interface Notificacion {
  id: number;
  miembroId: number;
  nombreMiembro: string;
  tipoNotificacion: 'VENCIMIENTO_MEMBRESIA' | 'PAGO_PENDIENTE' | 'BIENVENIDA' | 'GENERAL';
  titulo: string;
  mensaje: string;
  fechaEnvio: Date;
  leida: boolean;
  fechaLectura?: Date;
  enviada: boolean;
  fechaProgramada?: Date;
  pendiente: boolean;
}

export interface NotificacionRequest {
  miembroId: number;
  tipoNotificacion: 'VENCIMIENTO_MEMBRESIA' | 'PAGO_PENDIENTE' | 'BIENVENIDA' | 'GENERAL';
  titulo: string;
  mensaje: string;
  fechaProgramada?: Date;
}