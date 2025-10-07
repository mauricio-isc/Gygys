import { Component, OnInit } from "@angular/core";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { Router, ActivatedRoute } from "@angular/router"
import { AuthService } from "../../services/auth.service";
import Swal from 'sweetalert2';

@Component({
    selector: 'app-login',
    templateUrl: './login.component.html',
    styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit{

    loginForm!: FormGroup;
    loading = false;
    returnUrl: string = '';

    constructor(
        private fb: FormBuilder,
        private router: Router,
        private route: ActivatedRoute,
        private authService: AuthService
    ){}

    ngOnInit(): void {
        this.loginForm = this.fb.group({
            username: ['', [Validators.required, Validators.minLength(3)]],
            password: ['', [Validators.required, Validators.minLength(8)]]
        });

        //obtener la url si es que el retorno existe
        this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/dashboard';
    }

    onSubmit(): void{
        if(this.loginForm.valid){
            this.loading = true;

            this.authService.login(this.loginForm.value).subscribe({
                next: (response) => {
                    this.loading = false;
                    Swal.fire({
                        icon: 'success',
                        title: '¡Bienvenido!',
                        text: `Hola ${response.nombreCompleto}`,
                        timer: 2000,
                        showConfirmButton: false
                    });
                    this.router.navigate([this.returnUrl]);
                },
                error: (error) => {
                    this.loading = false;
                    Swal.fire({
                        icon: 'error',
                        title: 'Error de autenticación',
                        text: 'Usuario o contraseña incorrectos',
                        confirmButtonText: 'Intentar de nuevo'
                    });
                }
            });
        }
    }

    get f() {
        return this.loginForm.controls;
    }
}