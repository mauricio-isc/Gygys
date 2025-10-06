import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http'; 
import { Observable } from 'rxjs';
import { ApiResponse } from '../models/dashboard.model';
import { Miembro, MiembroRequest, MiembroResponse } from '../models/miembro.model';

@Injectable({
    providedIn: 'root'
    
})

export class MiembroService {
    private apiUrl = 'http://localhost:3000/api/members';

    constructor(private http: HttpClient){ }

    findAll(page:number = 0, size:number = 10): Observable<ApiResponse<any>> {
        const params = new HttpParams()
        .set('page', page.toString())
        .set('size', size.toString());

        return this.http.get<ApiResponse<any>>(this.apiUrl, { params });
    }

    search(search: string, page:number=0, size:number=10): Observable<ApiResponse<any>> {
        const params = new HttpParams()
        .set('search', search)
        .set('page', page.toString())
        .set('size', size.toString());

        return this.http.get<ApiResponse<any>>(`${this.apiUrl}/search`, { params });
    }

    findById(id: number): Observable<MiembroResponse>{
        return this.http.get<MiembroResponse>(`${this.apiUrl}/${id}`);
    }

    create(request: MiembroRequest): Observable<Miembro>{
        return this.http.post<MiembroResponse>(this.apiUrl, request);
    }

    update(id: number, request: MiembroRequest): Observable<MiembroResponse>{
        return this.http.put<MiembroResponse>(`${this.apiUrl}/${id}`, request);
    }

    delete(id: number): Observable<void>{
        return this.http.delete<void>(`${this.apiUrl}/${id}`);
    }

    findAllActive(): Observable<Miembro[]> {
        return this.http.get<Miembro[]>(`${this.apiUrl}/active`);
    }

    getRecentMembers(): Observable<Miembro[]> {
        return this.http.get<Miembro[]>(`${this.apiUrl}/recent`);
    }    
}