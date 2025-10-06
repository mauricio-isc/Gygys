import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { jwtDecode } from 'jwt-decode';
import { AuthRequest, AuthResponse, User } from '../models/auth.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private currentUserSubject = new BehaviorSubject<User | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  private apiUrl = 'http://localhost:8080/api/auth';
  private tokenKey = 'auth_token';

  constructor(private http: HttpClient) {
    this.loadCurrentUser();
  }

  login(request: AuthRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, request).pipe(
      tap(response => {
        localStorage.setItem(this.tokenKey, response.token);
        this.currentUserSubject.next({
          id: response.id,
          username: response.username,
          email: response.email,
          nombre: response.nombre,
          apellido: response.apellido,
          nombreCompleto: response.nombreCompleto,
          ultimoAcceso: response.ultimoAcceso
        });
      })
    );
  }

  logout(): void {
    localStorage.removeItem(this.tokenKey);
    this.currentUserSubject.next(null);
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  isAuthenticated(): boolean {
    const token = this.getToken();
    if (!token) return false;

    try {
      const decoded: any = jwtDecode(token);
      const currentTime = Date.now() / 1000;
      return decoded.exp > currentTime;
    } catch (error) {
      return false;
    }
  }

  getCurrentUser(): User | null {
    return this.currentUserSubject.value;
  }

  private loadCurrentUser(): void {
    const token = this.getToken();
    if (token && this.isAuthenticated()) {
      try {
        const decoded: any = jwtDecode(token);
        const user: User = {
          id: decoded.id,
          username: decoded.sub,
          email: decoded.email,
          nombre: decoded.nombre,
          apellido: decoded.apellido,
          nombreCompleto: decoded.nombreCompleto,
          ultimoAcceso: decoded.ultimoAcceso
        };
        this.currentUserSubject.next(user);
      } catch (error) {
        this.logout();
      }
    }
  }
}