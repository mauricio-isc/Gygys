import { Miembro } from './miembro.model';
import { Membresia } from './membresia.model';
import { Notificacion } from './notificacion.model';

export interface DashboardStats {
  totalMiembros: number;
  miembrosActivos: number;
  miembrosNuevosMes: number;
  membresiasActivas: number;
  membresiasVencidas: number;
  membresiasPorVencer: number;
  ingresosMes: number;
  ingresosAnio: number;
  notificacionesPendientes: number;
  notificacionesLeidas: number;
  ultimosMiembrosRegistrados: Miembro[];
  membresiasProximasAVencer: Membresia[];
  ultimasNotificaciones: Notificacion[];
  ingresosPorMes: IngresoMensual[];
}

export interface IngresoMensual {
  mes: string;
  ingreso: number;
  cantidadMembresias: number;
}

export interface ApiResponse<T> {
  success: boolean;
  data: T;
  message?: string;
  errors?: any;
}