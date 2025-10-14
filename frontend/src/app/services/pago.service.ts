import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface PagoDetalle {
  id: number;
  monto: number;
  fechaPago: string;
  metodoPago: string;
  referenciaPago: string;
  notas: string;
  nombreMiembro: string;
  tipoMembresia: string;
  fechaInicioMembresia: string;
  fechaFinMembresia: string;
}

@Injectable({
  providedIn: 'root'
})
export class PagoService {
  private apiUrl = 'http://localhost:8080/api/pagos';

  constructor(private http: HttpClient) { }

  getPagosPorMiembro(miembroId: number): Observable<PagoDetalle[]> {
    return this.http.get<PagoDetalle[]>(`${this.apiUrl}/miembro/${miembroId}`);
  }
}