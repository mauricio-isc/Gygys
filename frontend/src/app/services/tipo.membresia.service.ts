import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { TipoMembresia } from "../models/tipo-membresia.model";

@Injectable({
    providedIn: 'root'
})
export class TipoMembresiaService{
    
    private apiUrl = 'http://localhost:8080/api/type'

    constructor(private http: HttpClient){}

    findAll(): Observable<TipoMembresia[]>{
        return this.http.get<TipoMembresia[]>(this.apiUrl);
    }

    findById(id: number): Observable<TipoMembresia>{
        return this.http.get<TipoMembresia>(`${this.apiUrl}/${id}`);
    }

    findActive(): Observable<TipoMembresia[]>{
        return this.http.get<TipoMembresia[]>(`${this.apiUrl}/activos`);
    }

    create(TipoMembresia: Omit<TipoMembresia, 'id'>): Observable<TipoMembresia>{
        return this.http.post<TipoMembresia>(this.apiUrl, TipoMembresia);
    }

    update(id: number, TipoMembresia: Partial<TipoMembresia>): Observable<TipoMembresia>{
        return this.http.put<TipoMembresia>(`${this.apiUrl}/${id}`, TipoMembresia);
    }

    delete(id:number):Observable<void>{
        return this.http.delete<void>(`${this.apiUrl}/${id}`);
    }
}