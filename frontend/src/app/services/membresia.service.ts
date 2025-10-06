import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { Membresia, MembresiaRequest, ActivateMembershipRequest } from "../models/membresia.model";

@Injectable({
    providedIn: 'root'
})

export class MembresiaService {
    private apiUrl = 'http://localhost:8080/api/memberships';

    constructor(private http: HttpClient) { }

    findAll(): Observable<Membresia[]> {
        return this.http.get<Membresia[]>(this.apiUrl);
    }

    findById(id: number): Observable<Membresia> {
        return this.http.get<Membresia>(`${this.apiUrl}/${id}`);
    }


    create(request: MembresiaRequest): Observable<Membresia> {
        return this.http.post<Membresia>(this.apiUrl, request);
    }

    activateMembership(miembroId: number, tipoMembresiaId: number, precioPagado: number): Observable<Membresia> {
        const params = {
            miembroId: miembroId.toString(),
            tipoMembresiaId: tipoMembresiaId.toString(),
            precioPagado: precioPagado.toString()
        };
        return this.http.post<Membresia>(`${this.apiUrl}/activate`, null, { params });
    }

    findExperingMemberships(): Observable<Membresia[]> {
        return this.http.get<Membresia[]>(`${this.apiUrl}/expiring`);
    }


    getMembershipStats(): Observable<any> {
        return this.http.get(`${this.apiUrl}/stats`);
    }

    updateMembershipStatus(): Observable<void> {
        return this.http.post<void>(`${this.apiUrl}/update-status`, null);
    }

}