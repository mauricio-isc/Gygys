import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable,tap } from "rxjs";
import { Notificacion } from "../models/notificacion.model";

@Injectable({
    providedIn: 'root'
})

export class NotificacionService{

    private apiUrl = 'http://localhost:8080/api/notifications';

    constructor(private http: HttpClient) {}

    findAll(): Observable<Notificacion[]> {
        return this.http.get<Notificacion[]>(this.apiUrl);
    }

    findUnread(): Observable<Notificacion[]> {
        return this.http.get<Notificacion[]>(`${this.apiUrl}/unread`);
    }

    findByMember(memberId: number): Observable<Notificacion[]> {
        return this.http.get<Notificacion[]>(`${this.apiUrl}/member/${memberId}`);
    }

    markAsRead(id: number): Observable<Notificacion> {
        return this.http.put<Notificacion>(`${this.apiUrl}/${id}/read`, null);
    }

    sendPendingNotifications(): Observable<void> {
        return this.http.post<void>(`${this.apiUrl}/send-pending`, null);
    }

    runAutomaticNotifications(): Observable<string> {
        return this.http.post<string>(`${this.apiUrl}/run-automatic`, null);
    }

   /* getNotificationStats(): Observable<any> {
    const token = localStorage.getItem('auth_token'); // <--- debug token
    console.log('Token being sent to /stats:', token);

    

    return this.http.get(`${this.apiUrl}/stats`, {
        headers: {
        Authorization: `Bearer ${token}`
        },
        observe: 'response'
    }).pipe(
        tap({
        next: (res) => console.log('Response received:', res),
        error: (err) => console.error('Error loading stats:', err)
        })
    );
    }
*/
}