export interface Pago{
    id: number;
    membresiaId: number;
    monto: number;
    fechaPago: Date;
    metodoPago: 'EFECTIVO' | 'TARJETA' | 'TRANSFERENCIA' | 'OTRO';
    referenciaPago?: string;
    notas?: string;
    registradoPor: number;
    nombreRegistradoPor?: number;

    nombreMiembro?: string;
    tipoMembresia?: string;
}