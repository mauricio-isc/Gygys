export interface AuthResponse {
    username: string;
    password: string;
}

export interface AuthRequest {
    token: string;
    tipoToken: string;
    id: number;
    username: string;
    email: string;
    nombre: string;
    apellido: string;
    nombreCompleto: string;
    ultimoAcceso: Date;
}

export interface User{
    id: number;
    username: string;
    email: string;
    nombre: string;
    apellido: string;
    nombreCompleto: string;
    ultimoAcceso: Date;
}