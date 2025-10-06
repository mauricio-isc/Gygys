import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { DashboardStats } from '../models/dashboard.model';

@Injectable({
    providedIn: 'root'
})
export class DashboardService {

    private apiUrl = 'http://localhost:8080/api/dashboard';

    constructor(private http: HttpClient) { }

    getDashboardStats(): Observable<DashboardStats> {
        return this.http.get<DashboardStats>(`${this.apiUrl}/stats`);
    }

    updateSystemStatus(): Observable<void>{
        return this.http.post<void>(`${this.apiUrl}/update-system`, null);
    }
}   